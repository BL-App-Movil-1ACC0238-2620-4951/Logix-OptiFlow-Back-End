package com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "inventories")
public class InventoryEntity {

  @Id
  private UUID id;

  @Column(name = "optical_store_id", nullable = false, unique = true)
  private UUID opticalStoreId;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOpticalStoreId() {
    return opticalStoreId;
  }

  public void setOpticalStoreId(UUID opticalStoreId) {
    this.opticalStoreId = opticalStoreId;
  }
}
