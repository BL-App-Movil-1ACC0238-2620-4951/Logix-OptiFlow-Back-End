package com.optiflow.platform.clinical.infrastructure.outboundservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optiflow.platform.clinical.application.outboundservices.NotifiedPayment;
import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentNotification;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.infrastructure.configuration.MercadoPagoProperties;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Anti-corruption layer for Mercado Pago Checkout Pro: it creates the payment preferences, checks
 * the signature of the notifications, reads the notified payment and translates it, keeping the
 * model of the provider (preferences, payment statuses and codes) out of the domain.
 */
@Component
public class PaymentGatewayAdapter implements PaymentGatewayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayAdapter.class);
  private static final String CURRENCY = "PEN";
  private static final String PAYMENT_TOPIC = "payment";
  private static final String HMAC = "HmacSHA256";

  private final MercadoPagoProperties properties;
  private final RestClient restClient;

  public PaymentGatewayAdapter(
      MercadoPagoProperties properties, RestClient.Builder restClientBuilder) {
    this.properties = properties;
    this.restClient = restClientBuilder.baseUrl(properties.baseUrl()).build();
  }

  @Override
  public PaymentPreference createPreference(SaleId saleId, Money amount, String title) {
    String backUrl = properties.backUrl();
    PreferenceRequest request = new PreferenceRequest(
        List.of(new PreferenceItem(title, 1, CURRENCY, amount.amount())),
        saleId.value().toString(),
        new BackUrls(backUrl, backUrl, backUrl),
        properties.notificationUrl());
    try {
      PreferenceResponse response = restClient.post()
          .uri("/checkout/preferences")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
          .contentType(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .body(PreferenceResponse.class);
      if (response == null || response.id() == null || response.initPoint() == null) {
        throw new RestClientException("Mercado Pago returned an empty preference.");
      }
      return new PaymentPreference(response.id(), response.initPoint());
    } catch (RestClientException exception) {
      LOGGER.warn("Mercado Pago could not create the preference of sale {}.", saleId, exception);
      throw new DomainException("The payment gateway is not available. Try again later.", 503);
    }
  }

  @Override
  public Optional<NotifiedPayment> translate(PaymentNotification notification) {
    if (!PAYMENT_TOPIC.equals(notification.type()) || notification.resourceId() == null) {
      return Optional.empty();
    }
    if (!isAuthentic(notification)) {
      throw new DomainException("The payment notification signature is invalid.", 401);
    }
    PaymentDetails payment = findPayment(notification.resourceId());
    PaymentStatus status = switch (String.valueOf(payment.status())) {
      case "approved" -> PaymentStatus.APPROVED;
      case "rejected" -> PaymentStatus.REJECTED;
      default -> null;
    };
    UUID saleId = saleIdOf(payment.externalReference());
    if (status == null || saleId == null || payment.transactionAmount() == null) {
      LOGGER.info("Mercado Pago payment {} ({}) is not recorded.", payment.id(), payment.status());
      return Optional.empty();
    }
    return Optional.of(new NotifiedPayment(
        SaleId.of(saleId),
        "yape".equals(payment.paymentMethodId()) ? PaymentMethod.YAPE : PaymentMethod.CARD,
        Money.of(payment.transactionAmount()),
        String.valueOf(payment.id()),
        status,
        status == PaymentStatus.REJECTED ? payment.statusDetail() : null));
  }

  /**
   * Mercado Pago signs {@code id:<data.id>;request-id:<x-request-id>;ts:<ts>;} with the webhook
   * secret and sends {@code ts=<ts>,v1=<hmac>} in the {@code x-signature} header.
   */
  boolean isAuthentic(PaymentNotification notification) {
    String secret = properties.webhookSecret();
    if (secret == null || secret.isBlank() || notification.signature() == null) {
      return false;
    }
    String timestamp = null;
    String hash = null;
    for (String part : notification.signature().split(",")) {
      String[] pair = part.trim().split("=", 2);
      if (pair.length == 2 && pair[0].equals("ts")) {
        timestamp = pair[1];
      } else if (pair.length == 2 && pair[0].equals("v1")) {
        hash = pair[1];
      }
    }
    if (timestamp == null || hash == null) {
      return false;
    }
    StringBuilder manifest = new StringBuilder()
        .append("id:").append(notification.resourceId().toLowerCase(Locale.ROOT)).append(';');
    if (notification.requestId() != null && !notification.requestId().isBlank()) {
      manifest.append("request-id:").append(notification.requestId()).append(';');
    }
    manifest.append("ts:").append(timestamp).append(';');
    return MessageDigest.isEqual(
        hmac(secret, manifest.toString()).getBytes(StandardCharsets.UTF_8),
        hash.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
  }

  private PaymentDetails findPayment(String paymentId) {
    try {
      PaymentDetails payment = restClient.get()
          .uri("/v1/payments/{id}", paymentId)
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken())
          .retrieve()
          .body(PaymentDetails.class);
      if (payment == null) {
        throw new RestClientException("Mercado Pago returned an empty payment.");
      }
      return payment;
    } catch (RestClientException exception) {
      LOGGER.warn("Mercado Pago payment {} could not be read.", paymentId, exception);
      throw new DomainException("The payment gateway is not available. Try again later.", 503);
    }
  }

  private static UUID saleIdOf(String externalReference) {
    try {
      return externalReference == null ? null : UUID.fromString(externalReference);
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  private static String hmac(String secret, String content) {
    try {
      Mac mac = Mac.getInstance(HMAC);
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC));
      return HexFormat.of().formatHex(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    } catch (GeneralSecurityException exception) {
      throw new IllegalStateException("HmacSHA256 is not available.", exception);
    }
  }

  /** Charge made by the sales advisor; no real provider is involved. */
  @Override
  public String authorize(PaymentMethod method, Money amount, String transactionReference) {
    String reference = transactionReference == null || transactionReference.isBlank()
        ? method.name() + "-" + UUID.randomUUID().toString()
            .substring(0, 8).toUpperCase(Locale.ROOT)
        : transactionReference.trim();
    LOGGER.info("Payment of {} via {} authorized with reference {}.", amount, method, reference);
    return reference;
  }

  private String accessToken() {
    if (properties.accessToken() == null || properties.accessToken().isBlank()) {
      throw new DomainException("The payment gateway is not configured.", 503);
    }
    return properties.accessToken();
  }

  record PreferenceRequest(
      List<PreferenceItem> items,
      @JsonProperty("external_reference") String externalReference,
      @JsonProperty("back_urls") BackUrls backUrls,
      @JsonProperty("notification_url") String notificationUrl) {
  }

  record PreferenceItem(
      String title,
      int quantity,
      @JsonProperty("currency_id") String currencyId,
      @JsonProperty("unit_price") BigDecimal unitPrice) {
  }

  record BackUrls(String success, String failure, String pending) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record PaymentDetails(
      Long id,
      String status,
      @JsonProperty("status_detail") String statusDetail,
      @JsonProperty("external_reference") String externalReference,
      @JsonProperty("transaction_amount") BigDecimal transactionAmount,
      @JsonProperty("payment_method_id") String paymentMethodId) {
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record PreferenceResponse(String id, @JsonProperty("init_point") String initPoint) {
  }
}
