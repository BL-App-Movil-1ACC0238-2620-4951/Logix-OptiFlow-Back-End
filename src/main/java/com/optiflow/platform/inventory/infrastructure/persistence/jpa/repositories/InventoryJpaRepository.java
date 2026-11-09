package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.InventoryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, UUID> {

  Optional<InventoryEntity> findByOpticalStoreId(UUID opticalStoreId);
}
