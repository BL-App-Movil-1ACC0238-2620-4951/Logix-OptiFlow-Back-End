package com.optiflow.platform.clinical.application.commandhandlers;

import com.optiflow.platform.clinical.application.outboundservices.BookedAppointment;
import com.optiflow.platform.clinical.application.outboundservices.ExternalAppointmentService;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.events.ElectronicReceiptIssued;
import com.optiflow.platform.clinical.domain.model.events.SaleWasClosed;
import com.optiflow.platform.clinical.domain.model.valueobjects.SoldProduct;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Closes a paid sale, issues its electronic receipt and hands it over to production. */
@Service
public class CloseSaleHandler {

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final ClinicalRecordRepository clinicalRecordRepository;
  private final ExternalAppointmentService externalAppointmentService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public CloseSaleHandler(
      SaleRepository saleRepository,
      QuotationRepository quotationRepository,
      ClinicalRecordRepository clinicalRecordRepository,
      ExternalAppointmentService externalAppointmentService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.saleRepository = saleRepository;
    this.quotationRepository = quotationRepository;
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.externalAppointmentService = externalAppointmentService;
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
        opticalStoreOf(quotation),
        soldProducts(quotation),
        now));
    eventPublisher.publish(new ElectronicReceiptIssued(
        receipt.id(), sale.id(), receipt.receiptNumber(), receipt.totalAmount(), now));
    return sale;
  }

  private UUID opticalStoreOf(Quotation quotation) {
    return clinicalRecordRepository.findById(quotation.clinicalRecordId())
        .flatMap(clinicalRecord -> externalAppointmentService.findById(clinicalRecord.appointmentId()))
        .map(BookedAppointment::opticalStoreId)
        .orElse(null);
  }

  private static List<SoldProduct> soldProducts(Quotation quotation) {
    return quotation.items().stream()
        .filter(item -> item.productSku() != null && !item.productSku().isBlank())
        .map(item -> new SoldProduct(item.productSku(), item.quantity()))
        .toList();
  }
}
