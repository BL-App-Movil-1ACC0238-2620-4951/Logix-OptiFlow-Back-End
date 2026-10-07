package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.OpticalPrescriptionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpticalPrescriptionJpaRepository extends JpaRepository<OpticalPrescriptionEntity, UUID> {
}
