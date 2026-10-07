package com.optiflow.platform.production.infrastructure.outboundservices;

import com.optiflow.platform.clinical.application.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.application.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.application.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.application.services.ClinicalRecordApplicationService;
import com.optiflow.platform.clinical.application.services.QuotationApplicationService;
import com.optiflow.platform.clinical.application.services.SaleApplicationService;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.Sale;
import com.optiflow.platform.clinical.domain.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.domain.valueobjects.SaleStatus;
import com.optiflow.platform.production.application.services.ClosedSale;
import com.optiflow.platform.production.application.services.ExternalSaleService;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.searchbooking.application.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.application.services.AppointmentApplicationService;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption layer between Clinical & Commercial (upstream) and Production & Tracking:
 * reads the closed sale and translates its prescription into lens specifications, leaving out
 * prices, payments and billing data that manufacturing does not need.
 */
@Component
public class ClinicalCommercialSaleAdapter implements ExternalSaleService {

  private static final String DEFAULT_LENS = "Lunas según la cotización aprobada";

  private final SaleApplicationService saleApplicationService;
  private final QuotationApplicationService quotationApplicationService;
  private final ClinicalRecordApplicationService clinicalRecordApplicationService;
  private final AppointmentApplicationService appointmentApplicationService;

  public ClinicalCommercialSaleAdapter(
      SaleApplicationService saleApplicationService,
      QuotationApplicationService quotationApplicationService,
      ClinicalRecordApplicationService clinicalRecordApplicationService,
      AppointmentApplicationService appointmentApplicationService) {
    this.saleApplicationService = saleApplicationService;
    this.quotationApplicationService = quotationApplicationService;
    this.clinicalRecordApplicationService = clinicalRecordApplicationService;
    this.appointmentApplicationService = appointmentApplicationService;
  }

  @Override
  public Optional<ClosedSale> findSale(SaleId saleId) {
    Sale sale;
    try {
      sale = saleApplicationService.getById(new GetSaleByIdQuery(
          com.optiflow.platform.clinical.domain.valueobjects.SaleId.of(saleId.value())));
    } catch (DomainException exception) {
      if (exception.status() == 404) {
        return Optional.empty();
      }
      throw exception;
    }
    Quotation quotation =
        quotationApplicationService.getById(new GetQuotationByIdQuery(sale.quotationId()));
    ClinicalRecord clinicalRecord = clinicalRecordApplicationService.getById(
        new GetClinicalRecordByIdQuery(quotation.clinicalRecordId()));
    return Optional.of(new ClosedSale(
        saleId,
        PatientId.of(sale.patientId().value()),
        findOpticalStore(clinicalRecord),
        sale.status() == SaleStatus.CLOSED,
        lensSpecifications(clinicalRecord, quotation)));
  }

  private OpticalStoreId findOpticalStore(ClinicalRecord clinicalRecord) {
    try {
      return OpticalStoreId.of(appointmentApplicationService
          .getById(new GetAppointmentQuery(
              AppointmentId.of(clinicalRecord.appointmentId().value())))
          .appointment()
          .opticalStoreId()
          .value());
    } catch (DomainException exception) {
      return null;
    }
  }

  private static List<String> lensSpecifications(
      ClinicalRecord clinicalRecord, Quotation quotation) {
    Optional<OpticalPrescription> prescription = clinicalRecord.prescription();
    if (prescription.isPresent()) {
      OpticalPrescription value = prescription.get();
      String extras = extras(value);
      return List.of(
          limit(lens("OD", value.sphereOd(), value.cylinderOd(), value.axisOd()) + extras),
          limit(lens("OS", value.sphereOs(), value.cylinderOs(), value.axisOs()) + extras));
    }
    List<String> lensItems = quotation.items().stream()
        .filter(item -> item.itemType() == QuotationItemType.LENS)
        .map(item -> item.description())
        .toList();
    return lensItems.isEmpty() ? List.of(DEFAULT_LENS) : lensItems;
  }

  private static String lens(String eye, BigDecimal sphere, BigDecimal cylinder, Integer axis) {
    return "%s: ESF %s CIL %s EJE %d°".formatted(eye, signed(sphere), signed(cylinder), axis);
  }

  private static String extras(OpticalPrescription prescription) {
    StringBuilder extras = new StringBuilder();
    if (prescription.addition() != null) {
      extras.append(" | ADD ").append(signed(prescription.addition()));
    }
    if (prescription.treatment() != null) {
      extras.append(" | ").append(prescription.treatment());
    }
    if (prescription.recommendedFrameType() != null) {
      extras.append(" | Montura: ").append(prescription.recommendedFrameType());
    }
    return extras.toString();
  }

  /** Lens specifications are stored with at most 500 characters. */
  private static String limit(String specifications) {
    return specifications.length() <= 500 ? specifications : specifications.substring(0, 500);
  }

  private static String signed(BigDecimal value) {
    return String.format(Locale.ROOT, "%+.2f", value);
  }
}
