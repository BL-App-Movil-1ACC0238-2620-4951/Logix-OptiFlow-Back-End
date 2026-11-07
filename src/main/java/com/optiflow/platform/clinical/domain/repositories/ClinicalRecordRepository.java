package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository {

  void save(ClinicalRecord clinicalRecord);

  Optional<ClinicalRecord> findById(ClinicalRecordId id);

  Optional<ClinicalRecord> findByAppointmentId(AppointmentId appointmentId);

  List<ClinicalRecord> findByPatientId(PatientId patientId);
}
