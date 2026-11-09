package com.optiflow.platform.clinical.application.commandhandlers;

import com.optiflow.platform.clinical.application.outboundservices.PaymentGatewayService;
import com.optiflow.platform.clinical.application.outboundservices.PaymentPreference;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.commands.CreatePaymentPreferenceCommand;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePaymentPreferenceHandler {

  /** Title of the payment shown to the patient on the gateway page. */
  static final String PAYMENT_TITLE = "Pedido OptiFlow";

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final PaymentGatewayService paymentGatewayService;

  public CreatePaymentPreferenceHandler(
      SaleRepository saleRepository,
      QuotationRepository quotationRepository,
      PaymentGatewayService paymentGatewayService) {
    this.saleRepository = saleRepository;
    this.quotationRepository = quotationRepository;
    this.paymentGatewayService = paymentGatewayService;
  }

  @Transactional
  public PaymentPreference handle(CreatePaymentPreferenceCommand command) {
    Sale sale = saleRepository.findById(command.saleId())
        .orElseThrow(SaleNotFoundException::new);
    if (command.patientId() != null && !sale.belongsTo(command.patientId())) {
      throw new DomainException("You cannot pay another patient's sale.", 403);
    }
    sale.ensurePendingPayment();
    Quotation quotation = quotationRepository.findById(sale.quotationId())
        .orElseThrow(QuotationNotFoundException::new);

    PaymentPreference preference =
        paymentGatewayService.createPreference(sale.id(), quotation.total(), PAYMENT_TITLE);
    sale.attachPaymentPreference(preference.id());
    saleRepository.save(sale);
    return preference;
  }
}
