package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.events.PaymentRecorded;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.clinical.domain.valueobjects.Payment;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordPaymentHandler {

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final PaymentGatewayService paymentGatewayService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RecordPaymentHandler(
      SaleRepository saleRepository,
      QuotationRepository quotationRepository,
      PaymentGatewayService paymentGatewayService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.saleRepository = saleRepository;
    this.quotationRepository = quotationRepository;
    this.paymentGatewayService = paymentGatewayService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Sale handle(RecordPaymentCommand command) {
    Sale sale = saleRepository.findById(command.saleId())
        .orElseThrow(SaleNotFoundException::new);
    Quotation quotation = quotationRepository.findById(sale.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    sale.ensurePaymentCanBeRecorded(command.amount(), quotation.total());

    String transactionReference = paymentGatewayService.authorize(
        command.method(), command.amount(), command.transactionReference());
    Instant now = clock.instant();
    sale.recordPayment(
        new Payment(command.method(), command.amount(), transactionReference, now),
        quotation.total());
    saleRepository.save(sale);
    eventPublisher.publish(new PaymentRecorded(sale.id(), command.method(), command.amount(), now));
    return sale;
  }
}
