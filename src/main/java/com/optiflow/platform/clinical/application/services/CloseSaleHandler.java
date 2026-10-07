package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.events.ElectronicReceiptIssued;
import com.optiflow.platform.clinical.domain.events.SaleWasClosed;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Closes a paid sale, issues its electronic receipt and hands it over to production. */
@Service
public class CloseSaleHandler {

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public CloseSaleHandler(
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
  public Sale handle(CloseSaleCommand command) {
    Sale sale = saleRepository.findById(command.saleId())
        .orElseThrow(SaleNotFoundException::new);
    Quotation quotation = quotationRepository.findById(sale.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    Instant now = clock.instant();
    String receiptNumber = ElectronicReceipt.formatNumber(saleRepository.countIssuedReceipts() + 1);
    ElectronicReceipt receipt = sale.close(receiptNumber, now);
    saleRepository.save(sale);

    eventPublisher.publish(new SaleWasClosed(
        sale.id(),
        sale.quotationId(),
        quotation.clinicalRecordId(),
        sale.patientId(),
        receipt.totalAmount(),
        now));
    eventPublisher.publish(new ElectronicReceiptIssued(
        receipt.id(), sale.id(), receipt.receiptNumber(), receipt.totalAmount(), now));
    return sale;
  }
}
