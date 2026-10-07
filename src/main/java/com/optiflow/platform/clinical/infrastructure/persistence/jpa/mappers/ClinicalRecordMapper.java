package com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.entities.MedicalHistory;
import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordStatus;
import com.optiflow.platform.clinical.domain.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.ClinicalRecordEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.MedicalHistoryEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.OpticalPrescriptionEntity;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ClinicalRecordMapper {

  /** Medical history lists are stored one entry per line. */
  private static final String ENTRY_SEPARATOR = "\n";

  public ClinicalRecord toDomain(
      ClinicalRecordEntity entity,
      MedicalHistoryEntity medicalHistoryEntity,
      OpticalPrescriptionEntity prescriptionEntity) {
    return ClinicalRecord.reconstitute(
        ClinicalRecordId.of(entity.getId()),
        PatientId.of(entity.getPatientId()),
        AppointmentId.of(entity.getAppointmentId()),
        entity.getExaminationDate(),
        entity.getObservations(),
        ClinicalRecordStatus.valueOf(entity.getStatus()),
        medicalHistoryEntity == null ? null : toMedicalHistory(medicalHistoryEntity),
        prescriptionEntity == null ? null : toPrescription(prescriptionEntity));
  }

  public ClinicalRecordEntity toEntity(ClinicalRecord clinicalRecord) {
    ClinicalRecordEntity entity = new ClinicalRecordEntity();
    entity.setId(clinicalRecord.id().value());
    entity.setPatientId(clinicalRecord.patientId().value());
    entity.setAppointmentId(clinicalRecord.appointmentId().value());
    entity.setExaminationDate(clinicalRecord.examinationDate());
    entity.setObservations(clinicalRecord.observations());
    entity.setStatus(clinicalRecord.status().name());
    return entity;
  }

  public MedicalHistoryEntity toMedicalHistoryEntity(
      ClinicalRecordId clinicalRecordId, MedicalHistory medicalHistory) {
    MedicalHistoryEntity entity = new MedicalHistoryEntity();
    entity.setClinicalRecordId(clinicalRecordId.value());
    entity.setAllergies(joinEntries(medicalHistory.allergies()));
    entity.setPreviousConditions(joinEntries(medicalHistory.previousConditions()));
    entity.setFamilyOcularHistory(medicalHistory.familyOcularHistory());
    entity.setLastUpdated(medicalHistory.lastUpdated());
    return entity;
  }

  public OpticalPrescriptionEntity toPrescriptionEntity(
      ClinicalRecordId clinicalRecordId, OpticalPrescription prescription) {
    OpticalPrescriptionEntity entity = new OpticalPrescriptionEntity();
    entity.setClinicalRecordId(clinicalRecordId.value());
    entity.setSphereOd(prescription.sphereOd());
    entity.setSphereOs(prescription.sphereOs());
    entity.setCylinderOd(prescription.cylinderOd());
    entity.setCylinderOs(prescription.cylinderOs());
    entity.setAxisOd(prescription.axisOd().shortValue());
    entity.setAxisOs(prescription.axisOs().shortValue());
    entity.setAddition(prescription.addition());
    entity.setTreatment(prescription.treatment());
    entity.setRecommendedFrameType(prescription.recommendedFrameType());
    return entity;
  }

  private MedicalHistory toMedicalHistory(MedicalHistoryEntity entity) {
    return MedicalHistory.reconstitute(
        splitEntries(entity.getAllergies()),
        splitEntries(entity.getPreviousConditions()),
        entity.getFamilyOcularHistory(),
        entity.getLastUpdated());
  }

  private OpticalPrescription toPrescription(OpticalPrescriptionEntity entity) {
    return new OpticalPrescription(
        entity.getSphereOd(),
        entity.getSphereOs(),
        entity.getCylinderOd(),
        entity.getCylinderOs(),
        entity.getAxisOd().intValue(),
        entity.getAxisOs().intValue(),
        entity.getAddition(),
        entity.getTreatment(),
        entity.getRecommendedFrameType());
  }

  private static String joinEntries(List<String> entries) {
    return entries.isEmpty() ? null : String.join(ENTRY_SEPARATOR, entries);
  }

  private static List<String> splitEntries(String value) {
    return value == null || value.isBlank()
        ? List.of()
        : Arrays.asList(value.split(ENTRY_SEPARATOR));
  }
}
