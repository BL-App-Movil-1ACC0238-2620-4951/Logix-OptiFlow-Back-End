package com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class StockKey implements Serializable {

  @Column(name = "inventory_id", nullable = false)
  private UUID inventoryId;

  @Column(name = "frame_model_id", nullable = false)
  private UUID frameModelId;

  public StockKey() {
  }

  public StockKey(UUID inventoryId, UUID frameModelId) {
    this.inventoryId = inventoryId;
    this.frameModelId = frameModelId;
  }

  public UUID getInventoryId() {
    return inventoryId;
  }

  public void setInventoryId(UUID inventoryId) {
    this.inventoryId = inventoryId;
  }

  public UUID getFrameModelId() {
    return frameModelId;
  }

  public void setFrameModelId(UUID frameModelId) {
    this.frameModelId = frameModelId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof StockKey key)) {
      return false;
    }
    return Objects.equals(inventoryId, key.inventoryId)
        && Objects.equals(frameModelId, key.frameModelId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inventoryId, frameModelId);
  }
}
