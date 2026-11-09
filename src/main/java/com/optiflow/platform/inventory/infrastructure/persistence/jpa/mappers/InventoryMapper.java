package com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.entities.Stock;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.InventoryEntity;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockEntity;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockKey;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

  public Inventory toDomain(InventoryEntity entity, List<StockEntity> stocks) {
    return Inventory.reconstitute(
        InventoryId.of(entity.getId()),
        OpticalStoreId.of(entity.getOpticalStoreId()),
        stocks.stream()
            .map(stock -> new Stock(
                FrameModelId.of(stock.getId().getFrameModelId()),
                stock.getQuantity(),
                stock.getMinimumStock()))
            .toList());
  }

  public InventoryEntity toEntity(Inventory inventory) {
    InventoryEntity entity = new InventoryEntity();
    entity.setId(inventory.id().value());
    entity.setOpticalStoreId(inventory.opticalStoreId().value());
    return entity;
  }

  public List<StockEntity> toStockEntities(Inventory inventory) {
    return inventory.stocks().stream()
        .map(stock -> {
          StockEntity entity = new StockEntity();
          entity.setId(new StockKey(inventory.id().value(), stock.frameModelId().value()));
          entity.setQuantity(stock.quantity());
          entity.setMinimumStock(stock.minimumStock());
          return entity;
        })
        .toList();
  }
}
