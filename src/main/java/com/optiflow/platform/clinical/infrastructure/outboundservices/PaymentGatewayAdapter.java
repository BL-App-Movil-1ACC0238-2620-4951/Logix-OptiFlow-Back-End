package com.optiflow.platform.clinical.infrastructure.outboundservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.infrastructure.configuration.MercadoPagoProperties;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Anti-corruption layer for Mercado Pago Checkout Pro: it creates the payment preferences with
 * the REST API of the provider and keeps its model (preferences, items, back URLs) out of the
 * domain.
 */
@Component
public class PaymentGatewayAdapter implements PaymentGatewayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayAdapter.class);
  private static final String CURRENCY = "PEN";

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
  record PreferenceResponse(String id, @JsonProperty("init_point") String initPoint) {
  }
}
