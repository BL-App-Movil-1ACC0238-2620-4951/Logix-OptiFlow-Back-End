package com.optiflow.platform.clinical.application.queryservices;

import com.optiflow.platform.clinical.application.outboundservices.BookedAppointment;
import com.optiflow.platform.clinical.application.outboundservices.ExternalAppointmentService;
import com.optiflow.platform.clinical.application.results.ClosedSaleDetails;
import com.optiflow.platform.clinical.domain.exceptions.ClinicalRecordNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.QuotationNotFoundException;
import com.optiflow.platform.clinical.domain.exceptions.SaleNotFoundException;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.queries.GetClosedSaleQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetSalesByPatientIdQuery;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import com.optiflow.platform.clinical.domain.repositories.SaleRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleQueryService {

  private final SaleRepository saleRepository;
  private final QuotationRepository quotationRepository;
  private final ClinicalRecordRepository clinicalRecordRepository;
  private final ExternalAppointmentService externalAppointmentService;

  public SaleQueryService(
      SaleRepository saleRepository,
      QuotationRepository quotationRepository,
      ClinicalRecordRepository clinicalRecordRepository,
      ExternalAppointmentService externalAppointmentService) {
    this.saleRepository = saleRepository;
    this.quotationRepository = quotationRepository;
    this.clinicalRecordRepository = clinicalRecordRepository;
    this.externalAppointmentService = externalAppointmentService;
  }

  @Transactional(readOnly = true)
  public Sale handle(GetSaleByIdQuery query) {
    return saleRepository.findById(query.saleId()).orElseThrow(SaleNotFoundException::new);
  }

  /** Sales of the patient: the ones pending payment first, then the paid and closed ones. */
  @Transactional(readOnly = true)
  public List<Sale> handle(GetSalesByPatientIdQuery query) {
    return saleRepository.findByPatientId(query.patientId()).stream()
        .sorted(Comparator.comparing(Sale::status))
        .toList();
  }

  /** Sale with its quotation, clinical record and store, as Production & Tracking needs it. */
  @Transactional(readOnly = true)
  public ClosedSaleDetails handle(GetClosedSaleQuery query) {
    Sale sale =
        saleRepository.findById(query.saleId()).orElseThrow(SaleNotFoundException::new);
    Quotation quotation = quotationRepository.findById(sale.quotationId())
        .orElseThrow(QuotationNotFoundException::new);
    ClinicalRecord clinicalRecord = clinicalRecordRepository
        .findById(quotation.clinicalRecordId())
        .orElseThrow(ClinicalRecordNotFoundException::new);
    return new ClosedSaleDetails(
        sale,
        quotation,
        clinicalRecord,
        externalAppointmentService.findById(clinicalRecord.appointmentId())
            .map(BookedAppointment::opticalStoreId)
            .orElse(null));
  }
}
