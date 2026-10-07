package com.optiflow.platform.clinical.domain.repositories;

import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface ClinicalRecordRepository {

  void save(ClinicalRecord clinicalRecord);

  Optional<ClinicalRecord> findById(ClinicalRecordId id);

  Optional<ClinicalRecord> findByAppointmentId(AppointmentId appointmentId);

  List<ClinicalRecord> findByPatientId(PatientId patientId);
}
