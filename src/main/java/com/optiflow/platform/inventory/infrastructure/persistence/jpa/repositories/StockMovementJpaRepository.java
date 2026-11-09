package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementJpaRepository extends JpaRepository<StockMovementEntity, UUID> {

  List<StockMovementEntity>
      findByInventoryIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
          UUID inventoryId, Instant from, Instant to);

  List<StockMovementEntity>
      findByInventoryIdAndFrameModelIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
          UUID inventoryId, UUID frameModelId, Instant from, Instant to);
}
