package com.optiflow.platform.clinical;

import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.domain.repositories.ClinicalRecordRepository;
import com.optiflow.platform.clinical.domain.repositories.QuotationRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Saves an examined clinical record with its prescription and a quotation for it. */
public final class ClinicalRecordTestData {

  private ClinicalRecordTestData() {
  }

  public record SavedRecord(UUID clinicalRecordId, UUID quotationId) {}

  public static SavedRecord withQuotation(
      ClinicalRecordRepository clinicalRecordRepository,
      QuotationRepository quotationRepository,
      UUID patientId) {
    ClinicalRecord record = ClinicalRecord.register(
        PatientId.of(patientId), AppointmentId.generate(), Instant.now(), "Control anual");
    record.generatePrescription(new OpticalPrescription(
        new BigDecimal("-1.25"), new BigDecimal("-1.00"), BigDecimal.ZERO, BigDecimal.ZERO,
        0, 0, null, null, null));
    clinicalRecordRepository.save(record);
    Quotation quotation = Quotation.generate(record.id(), List.of(
        QuotationItem.create(QuotationItemType.LENS, null, "Lunas policarbonato",
            Money.of(new BigDecimal("169.00")), 1)));
    quotationRepository.save(quotation);
    return new SavedRecord(record.id().value(), quotation.id().value());
  }
}
