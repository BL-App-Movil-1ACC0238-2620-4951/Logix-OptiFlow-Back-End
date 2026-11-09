package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers.InventoryMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {

  private final InventoryJpaRepository inventoryJpaRepository;
  private final StockJpaRepository stockJpaRepository;
  private final InventoryMapper inventoryMapper;

  public InventoryRepositoryImpl(
      InventoryJpaRepository inventoryJpaRepository,
      StockJpaRepository stockJpaRepository,
      InventoryMapper inventoryMapper) {
    this.inventoryJpaRepository = inventoryJpaRepository;
    this.stockJpaRepository = stockJpaRepository;
    this.inventoryMapper = inventoryMapper;
  }

  @Override
  public void save(Inventory inventory) {
    inventoryJpaRepository.save(inventoryMapper.toEntity(inventory));
    stockJpaRepository.saveAll(inventoryMapper.toStockEntities(inventory));
  }

  @Override
  public Optional<Inventory> findByOpticalStoreId(OpticalStoreId opticalStoreId) {
    return inventoryJpaRepository.findByOpticalStoreId(opticalStoreId.value())
        .map(entity -> inventoryMapper.toDomain(
            entity, stockJpaRepository.findByIdInventoryId(entity.getId())));
  }
}
