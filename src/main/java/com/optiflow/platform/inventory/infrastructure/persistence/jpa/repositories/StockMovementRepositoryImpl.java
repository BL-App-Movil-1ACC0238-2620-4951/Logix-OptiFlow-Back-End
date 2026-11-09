package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.repositories.StockMovementRepository;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers.StockMovementMapper;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StockMovementRepositoryImpl implements StockMovementRepository {

  private final StockMovementJpaRepository stockMovementJpaRepository;
  private final StockMovementMapper stockMovementMapper;

  public StockMovementRepositoryImpl(
      StockMovementJpaRepository stockMovementJpaRepository,
      StockMovementMapper stockMovementMapper) {
    this.stockMovementJpaRepository = stockMovementJpaRepository;
    this.stockMovementMapper = stockMovementMapper;
  }

  @Override
  public void save(StockMovement movement) {
    stockMovementJpaRepository.save(stockMovementMapper.toEntity(movement));
  }

  @Override
  public List<StockMovement> findByPeriod(
      InventoryId inventoryId, Instant from, Instant to, FrameModelId frameModelId) {
    List<StockMovementEntity> movements = frameModelId == null
        ? stockMovementJpaRepository
            .findByInventoryIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                inventoryId.value(), from, to)
        : stockMovementJpaRepository
            .findByInventoryIdAndFrameModelIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                inventoryId.value(), frameModelId.value(), from, to);
    return movements.stream()
        .map(stockMovementMapper::toDomain)
        .toList();
  }
}
