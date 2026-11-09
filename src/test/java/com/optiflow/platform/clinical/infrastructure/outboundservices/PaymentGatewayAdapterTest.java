package com.optiflow.platform.clinical.infrastructure.outboundservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.optiflow.platform.clinical.application.outboundservices.NotifiedPayment;
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
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PaymentGatewayAdapterTest {

  private static final String BASE_URL = "https://api.mercadopago.com";
  private static final String SALE = "5b8f9d36-2c1a-4f53-9a43-3f0f3f7a1c10";
  private static final Money AMOUNT = Money.of(new BigDecimal("440.10"));
  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final RestClient.Builder restClientBuilder = RestClient.builder();
  private final MockRestServiceServer server =
      MockRestServiceServer.bindTo(restClientBuilder).build();

  @Test
  void createsThePreferenceWithTheSaleAmountAndReference() {
    server.expect(requestTo(BASE_URL + "/checkout/preferences"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer TEST-token"))
        .andExpect(jsonPath("$.external_reference").value(SALE))
        .andExpect(jsonPath("$.items[0].title").value("Pedido OptiFlow"))
        .andExpect(jsonPath("$.items[0].unit_price").value(440.10))
        .andExpect(jsonPath("$.items[0].currency_id").value("PEN"))
        .andExpect(jsonPath("$.back_urls.success").value("optiflow://payment-result"))
        .andExpect(jsonPath("$.notification_url").value("https://optiflow.test/notifications"))
        .andRespond(withSuccess("{\"id\":\"123-abc\",\"collector_id\":42,"
            + "\"init_point\":\"https://www.mercadopago.com.pe/checkout?pref_id=123-abc\"}",
            MediaType.APPLICATION_JSON));

    PaymentPreference preference = adapter("TEST-token")
        .createPreference(SaleId.of(UUID.fromString(SALE)), AMOUNT, "Pedido OptiFlow");

    assertEquals("123-abc", preference.id());
    assertEquals("https://www.mercadopago.com.pe/checkout?pref_id=123-abc", preference.initPoint());
    server.verify();
  }

  @Test
  void answersServiceUnavailableWhenTheGatewayFailsOrIsNotConfigured() {
    server.expect(requestTo(BASE_URL + "/checkout/preferences")).andRespond(withServerError());
    SaleId saleId = SaleId.of(UUID.fromString(SALE));

    DomainException failed = assertThrows(DomainException.class,
        () -> adapter("TEST-token").createPreference(saleId, AMOUNT, "Pedido OptiFlow"));
    assertEquals(503, failed.status());

    DomainException notConfigured = assertThrows(DomainException.class,
        () -> adapter("").createPreference(saleId, AMOUNT, "Pedido OptiFlow"));
    assertEquals(503, notConfigured.status());
    assertEquals("The payment gateway is not configured.", notConfigured.getMessage());
  }

  @Test
  void translatesTheNotifiedPaymentWhenTheSignatureIsValid() throws Exception {
    server.expect(requestTo(BASE_URL + "/v1/payments/1325046318"))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer TEST-token"))
        .andRespond(withSuccess(payment("1325046318", "approved", "accredited", "yape"), JSON));

    NotifiedPayment payment = adapter("TEST-token")
        .translate(notification("1325046318", sign("1325046318")))
        .orElseThrow();

    assertEquals(SaleId.of(UUID.fromString(SALE)), payment.saleId());
    assertEquals(PaymentMethod.YAPE, payment.method());
    assertEquals(AMOUNT, payment.amount());
    assertEquals("1325046318", payment.transactionReference());
    assertEquals(PaymentStatus.APPROVED, payment.status());
    assertNull(payment.statusDetail());
    server.verify();
  }

  @Test
  void keepsTheReasonOfARejectedCardPayment() throws Exception {
    server.expect(requestTo(BASE_URL + "/v1/payments/88"))
        .andRespond(withSuccess(
            payment("88", "rejected", "cc_rejected_insufficient_amount", "visa"), JSON));

    NotifiedPayment payment =
        adapter("TEST-token").translate(notification("88", sign("88"))).orElseThrow();

    assertEquals(PaymentMethod.CARD, payment.method());
    assertEquals(PaymentStatus.REJECTED, payment.status());
    assertEquals("cc_rejected_insufficient_amount", payment.statusDetail());
  }

  @Test
  void ignoresOtherTopicsAndPaymentsStillInProcess() throws Exception {
    assertTrue(adapter("TEST-token")
        .translate(new PaymentNotification("99", "merchant_order", null, null)).isEmpty());

    server.expect(requestTo(BASE_URL + "/v1/payments/7"))
        .andRespond(withSuccess(payment("7", "in_process", "pending_contingency", "visa"), JSON));
    assertTrue(adapter("TEST-token").translate(notification("7", sign("7"))).isEmpty());
  }

  @Test
  void rejectsANotificationWithAnInvalidSignature() throws Exception {
    PaymentGatewayAdapter adapter = adapter("TEST-token");

    DomainException exception = assertThrows(DomainException.class,
        () -> adapter.translate(notification("1325046318", "ts=1704908010,v1=0badc0de")));
    assertEquals(401, exception.status());
    assertFalse(adapter.isAuthentic(notification("1325046318", null)));
    assertFalse(adapter.isAuthentic(notification("1325046318", sign("other-id"))));
    assertTrue(adapter.isAuthentic(notification("1325046318", sign("1325046318"))));
  }

  private static PaymentNotification notification(String id, String signature) {
    return new PaymentNotification(id, "payment", signature, "req-1");
  }

  /** Signs as Mercado Pago does: HMAC-SHA256 of id, request id and timestamp. */
  private static String sign(String id) throws Exception {
    String timestamp = "1704908010";
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec("secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    byte[] hash = mac.doFinal(("id:" + id + ";request-id:req-1;ts:" + timestamp + ";")
        .getBytes(StandardCharsets.UTF_8));
    return "ts=" + timestamp + ",v1=" + HexFormat.of().formatHex(hash);
  }

  private static String payment(String id, String status, String detail, String method) {
    return "{\"id\":" + id + ",\"status\":\"" + status + "\",\"status_detail\":\"" + detail
        + "\",\"external_reference\":\"" + SALE + "\",\"transaction_amount\":440.10,"
        + "\"payment_method_id\":\"" + method + "\",\"currency_id\":\"PEN\"}";
  }

  private PaymentGatewayAdapter adapter(String accessToken) {
    return new PaymentGatewayAdapter(new MercadoPagoProperties(accessToken, "secret", BASE_URL,
        "https://optiflow.test/notifications", "optiflow://payment-result"), restClientBuilder);
  }
}
