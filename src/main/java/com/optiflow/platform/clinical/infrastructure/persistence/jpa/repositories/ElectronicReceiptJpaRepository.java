package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.ElectronicReceiptEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectronicReceiptJpaRepository extends JpaRepository<ElectronicReceiptEntity, UUID> {

  Optional<ElectronicReceiptEntity> findBySaleId(UUID saleId);
}
