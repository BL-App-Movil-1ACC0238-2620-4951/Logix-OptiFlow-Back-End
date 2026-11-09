package com.optiflow.platform.clinical.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.optiflow.platform.clinical.SaleTestData;
import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Checkout Pro flow with the payment gateway replaced by a mock. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckoutApiTest {

  private static final String INIT_POINT = "https://www.mercadopago.com.pe/checkout?pref_id=pref-1";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private QuotationRepository quotationRepository;

  @Autowired
  private SaleRepository saleRepository;

  @MockitoBean
  private PaymentGatewayService paymentGatewayService;

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
