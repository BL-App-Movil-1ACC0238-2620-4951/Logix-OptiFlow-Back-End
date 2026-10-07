package com.optiflow.booking.infrastructure.persistence.jpa;

import com.optiflow.booking.infrastructure.persistence.entity.PatientEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, UUID> {

  Optional<PatientEntity> findByEmail(String email);
}
