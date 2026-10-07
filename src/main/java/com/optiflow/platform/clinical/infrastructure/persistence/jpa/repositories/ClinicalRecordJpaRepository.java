package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.ClinicalRecordEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalRecordJpaRepository extends JpaRepository<ClinicalRecordEntity, UUID> {

  Optional<ClinicalRecordEntity> findByAppointmentId(UUID appointmentId);

  List<ClinicalRecordEntity> findByPatientId(UUID patientId);
}
