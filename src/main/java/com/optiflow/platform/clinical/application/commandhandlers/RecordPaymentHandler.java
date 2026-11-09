package com.optiflow.platform.clinical.application.commandhandlers;

import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.commands.RecordPaymentCommand;
import com.optiflow.platform.clinical.domain.model.events.PaymentRecorded;
import com.optiflow.platform.clinical.domain.model.events.PaymentRejected;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordPaymentHandler {

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RecordPaymentHandler(
      SaleRepository saleRepository,
      QuotationRepository quotationRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.saleRepository = saleRepository;
    this.quotationRepository = quotationRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Sale handle(RecordPaymentCommand command) {
    Sale sale = saleRepository.findById(command.saleId())
        .orElseThrow(SaleNotFoundException::new);
    if (command.transactionReference() != null && sale.hasPayment(command.transactionReference())) {
      return sale; // the gateway repeats its notifications
    }
    if (command.status() == PaymentStatus.REJECTED) {
      return reject(sale, command);
    }
    Quotation quotation = quotationRepository.findById(sale.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    sale.ensurePaymentCanBeRecorded(command.amount(), quotation.total());

    String transactionReference = command.transactionReference() == null
        || command.transactionReference().isBlank()
        ? cashReference()
        : command.transactionReference().trim();
    Instant now = clock.instant();
    sale.recordPayment(
        Payment.approved(command.method(), command.amount(), transactionReference, now),
        quotation.total());
    saleRepository.save(sale);
    eventPublisher.publish(new PaymentRecorded(sale.id(), command.method(), command.amount(), now));
    return sale;
  }

  /** Reference of a cash payment, which has no payment in the gateway. */
  private static String cashReference() {
    return PaymentMethod.CASH.name() + "-"
        + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
  }

  private Sale reject(Sale sale, RecordPaymentCommand command) {
    Instant now = clock.instant();
    sale.rejectPayment(new Payment(command.method(), command.amount(),
        command.transactionReference(), PaymentStatus.REJECTED, command.statusDetail(), now));
    saleRepository.save(sale);
    eventPublisher.publish(new PaymentRejected(
        sale.id(), command.method(), command.amount(), command.statusDetail(), now));
    return sale;
  }
}
