package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.MedicalHistoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryJpaRepository extends JpaRepository<MedicalHistoryEntity, UUID> {
}
