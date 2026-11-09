package com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementReason;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementType;
import com.optiflow.platform.inventory.domain.model.valueobjects.StockMovementId;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.StockMovementEntity;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

  public StockMovement toDomain(StockMovementEntity entity) {
    return new StockMovement(
        StockMovementId.of(entity.getId()),
        InventoryId.of(entity.getInventoryId()),
        FrameModelId.of(entity.getFrameModelId()),
        MovementType.valueOf(entity.getType()),
        MovementReason.valueOf(entity.getReason()),
        entity.getQuantity(),
        entity.getSaleId(),
        entity.getSupplierId() == null ? null : SupplierId.of(entity.getSupplierId()),
        entity.getPerformedBy(),
        entity.getCreatedAt());
  }

  public StockMovementEntity toEntity(StockMovement movement) {
    StockMovementEntity entity = new StockMovementEntity();
    entity.setId(movement.id().value());
    entity.setInventoryId(movement.inventoryId().value());
    entity.setFrameModelId(movement.frameModelId().value());
    entity.setType(movement.type().name());
    entity.setReason(movement.reason().name());
    entity.setQuantity(movement.quantity());
    entity.setSaleId(movement.saleId());
    entity.setSupplierId(movement.supplierId() == null ? null : movement.supplierId().value());
    entity.setPerformedBy(movement.performedBy());
    entity.setCreatedAt(movement.createdAt());
    return entity;
  }
}
