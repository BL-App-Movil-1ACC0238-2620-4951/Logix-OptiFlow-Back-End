package com.optiflow.platform.clinical.infrastructure.outboundservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.infrastructure.configuration.MercadoPagoProperties;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.util.UUID;
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

  private PaymentGatewayAdapter adapter(String accessToken) {
    return new PaymentGatewayAdapter(new MercadoPagoProperties(accessToken, "secret", BASE_URL,
        "https://optiflow.test/notifications", "optiflow://payment-result"), restClientBuilder);
  }
}
