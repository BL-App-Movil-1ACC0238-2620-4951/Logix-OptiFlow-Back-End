package com.optiflow.platform.clinical.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.optiflow.platform.clinical.SaleTestData;
import com.optiflow.platform.clinical.application.outboundservices.NotifiedPayment;
import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentNotification;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.events.PaymentRecorded;
import com.optiflow.platform.clinical.domain.model.events.PaymentRejected;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Checkout Pro flow with the payment gateway replaced by a mock. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
class CheckoutApiTest {

  private static final String INIT_POINT = "https://www.mercadopago.com.pe/checkout?pref_id=pref-1";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private QuotationRepository quotationRepository;

  @Autowired
  private SaleRepository saleRepository;

  @Autowired
  private ApplicationEvents applicationEvents;

  @MockitoBean
  private PaymentGatewayService paymentGatewayService;

  @Test
  void keepsTheSalePendingOnARejectionAndPaysItOnceWhenApproved() throws Exception {
    UUID saleId = SaleTestData.pendingSale(quotationRepository, saleRepository, UUID.randomUUID());
    SaleId id = SaleId.of(saleId);
    when(paymentGatewayService.translate(any()))
        .thenReturn(notified(id, "111", PaymentStatus.REJECTED, "cc_rejected_insufficient_amount"))
        .thenReturn(notified(id, "111", PaymentStatus.REJECTED, "cc_rejected_insufficient_amount"))
        .thenReturn(notified(id, "222", PaymentStatus.APPROVED, null))
        .thenReturn(notified(id, "222", PaymentStatus.APPROVED, null));

    notifyPayment().andExpect(status().isOk());
    notifyPayment().andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/sales/" + saleId)))
        .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
        .andExpect(jsonPath("$.payment.status").value("REJECTED"))
        .andExpect(jsonPath("$.payment.statusDetail").value("cc_rejected_insufficient_amount"));

    notifyPayment().andExpect(status().isOk());
    notifyPayment().andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/sales/" + saleId)))
        .andExpect(jsonPath("$.status").value("PAID"))
        .andExpect(jsonPath("$.payment.method").value("YAPE"))
        .andExpect(jsonPath("$.payment.transactionReference").value("222"))
        .andExpect(jsonPath("$.payment.status").value("APPROVED"));

    assertEquals(1, applicationEvents.stream(PaymentRejected.class)
        .filter(event -> event.saleId().equals(id)).count());
    assertEquals(1, applicationEvents.stream(PaymentRecorded.class)
        .filter(event -> event.saleId().equals(id)).count());
  }

  @Test
  void answersOkToNotificationsThatCannotBeRecorded() throws Exception {
    UUID saleId = SaleTestData.pendingSale(quotationRepository, saleRepository, UUID.randomUUID());
    when(paymentGatewayService.translate(any()))
        .thenReturn(notified(SaleId.of(UUID.randomUUID()), "333", PaymentStatus.APPROVED, null))
        .thenReturn(Optional.of(new NotifiedPayment(SaleId.of(saleId), PaymentMethod.CARD,
            Money.of(new BigDecimal("100.00")), "444", PaymentStatus.APPROVED, null)))
        .thenReturn(Optional.empty());

    notifyPayment().andExpect(status().isOk());
    notifyPayment().andExpect(status().isOk());
    notifyPayment().andExpect(status().isOk());
    mockMvc.perform(get(ApiV1.path("/sales/" + saleId)))
        .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
  }

  @Test
  void passesTheSignedNotificationToTheGatewayAndRejectsAnInvalidOne() throws Exception {
    when(paymentGatewayService.translate(
        new PaymentNotification("111", "payment", "ts=1704908010,v1=abc", "req-1")))
        .thenThrow(new DomainException("The payment notification signature is invalid.", 401));

    notifyPayment()
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("The payment notification signature is invalid."));
  }

  private ResultActions notifyPayment() throws Exception {
    return mockMvc.perform(post(ApiV1.path("/payment-notifications"))
        .param("data.id", "111")
        .param("type", "payment")
        .header("x-signature", "ts=1704908010,v1=abc")
        .header("x-request-id", "req-1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\":\"payment\",\"action\":\"payment.updated\",\"data\":{\"id\":\"111\"}}"));
  }

  private static Optional<NotifiedPayment> notified(
      SaleId saleId, String reference, PaymentStatus status, String statusDetail) {
    return Optional.of(new NotifiedPayment(saleId, PaymentMethod.YAPE, Money.of(SaleTestData.TOTAL),
        reference, status, statusDetail));
  }

  @Test
  void createsThePaymentPreferenceOfAPendingSale() throws Exception {
    UUID saleId = SaleTestData.pendingSale(quotationRepository, saleRepository, UUID.randomUUID());
    when(paymentGatewayService.createPreference(
        eq(SaleId.of(saleId)), eq(Money.of(SaleTestData.TOTAL)), any()))
        .thenReturn(new PaymentPreference("pref-1", INIT_POINT));

    mockMvc.perform(post(ApiV1.path("/sales/" + saleId + "/payment-preference")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("pref-1"))
        .andExpect(jsonPath("$.initPoint").value(INIT_POINT));

    assertEquals("pref-1",
        saleRepository.findById(SaleId.of(saleId)).orElseThrow().paymentPreferenceId().orElseThrow());
  }

  @Test
  void rejectsThePreferenceOfAnUnknownOrPaidSale() throws Exception {
    mockMvc.perform(post(ApiV1.path("/sales/" + UUID.randomUUID() + "/payment-preference")))
        .andExpect(status().isNotFound());

    UUID saleId = SaleTestData.pendingSale(quotationRepository, saleRepository, UUID.randomUUID());
    Sale sale = saleRepository.findById(SaleId.of(saleId)).orElseThrow();
    sale.recordPayment(Payment.approved(PaymentMethod.CASH, Money.of(SaleTestData.TOTAL), "CASH-1",
        Instant.now()), Money.of(SaleTestData.TOTAL));
    saleRepository.save(sale);
    mockMvc.perform(post(ApiV1.path("/sales/" + saleId + "/payment-preference")))
        .andExpect(status().isConflict());
  }
}
