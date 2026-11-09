package com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** One {@code Stock} of the {@code Inventory} aggregate. */
@Entity
@Table(name = "inventory_stocks")
public class StockEntity {

  @EmbeddedId
  private StockKey id;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "minimum_stock", nullable = false)
  private Integer minimumStock;

  public StockKey getId() {
    return id;
  }

  public void setId(StockKey id) {
    this.id = id;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Integer getMinimumStock() {
    return minimumStock;
  }

  public void setMinimumStock(Integer minimumStock) {
    this.minimumStock = minimumStock;
  }
}
