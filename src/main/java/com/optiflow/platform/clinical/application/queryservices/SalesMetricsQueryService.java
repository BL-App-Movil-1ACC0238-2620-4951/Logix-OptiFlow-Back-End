package com.optiflow.platform.clinical.application.queryservices;

import com.optiflow.platform.clinical.application.results.SalesMetrics;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.queries.GetSalesMetricsQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.Payment;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationStatus;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sales metrics of the whole business for the admin reports, in JSON or exported as CSV. */
@Service
public class SalesMetricsQueryService {

  private static final String CSV_HEADER =
      "sale_id,closed_at,receipt_number,payment_method,total_amount";

  private final ClinicalRecordRepository clinicalRecordRepository;
  private final QuotationRepository quotationRepository;
  private final SaleRepository saleRepository;
  private final Clock clock;

  public SalesMetricsQueryService(
      ClinicalRecordRepository clinicalRecordRepository,
      QuotationRepository quotationRepository,
      SaleRepository saleRepository,
      Clock clock) {
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.quotationRepository = quotationRepository;
    this.saleRepository = saleRepository;
    this.clock = clock;
  }

  /** Quotations have no date of their own, so they count in the period of their examination. */
  @Transactional(readOnly = true)
  public SalesMetrics handle(GetSalesMetricsQuery query) {
    if (query.from().isAfter(query.to())) {
      throw new DomainException("The start date must not be after the end date.", 400);
    }
    Instant from = query.from().atStartOfDay(clock.getZone()).toInstant();
    Instant to = query.to().plusDays(1).atStartOfDay(clock.getZone()).toInstant();
    List<ClinicalRecord> examined = clinicalRecordRepository.findExaminedBetween(from, to);
    List<Quotation> quotations = quotationRepository.findByClinicalRecordIds(
        examined.stream().map(ClinicalRecord::id).toList());
    return new SalesMetrics(
        query.from(),
        query.to(),
        examined.size(),
        count(quotations, QuotationStatus.APPROVED),
        count(quotations, QuotationStatus.REJECTED),
        saleRepository.findClosedBetween(from, to));
  }

  /** One row per sale closed in the period. */
  public String toCsv(SalesMetrics metrics) {
    StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
    for (Sale sale : metrics.closedSales()) {
      csv.append(String.join(",",
              sale.id().value().toString(),
              sale.closedAt().toString(),
              sale.receipt().map(ElectronicReceipt::receiptNumber).orElse(""),
              sale.payment().map(Payment::method).map(Enum::name).orElse(""),
              SalesMetrics.amountOf(sale).amount().toPlainString()))
          .append('\n');
    }
    return csv.toString();
  }

  private static long count(List<Quotation> quotations, QuotationStatus status) {
    return quotations.stream().filter(quotation -> quotation.status() == status).count();
  }
}
