package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockEntity;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockKey;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockJpaRepository extends JpaRepository<StockEntity, StockKey> {

  List<StockEntity> findByIdInventoryId(UUID inventoryId);
}
