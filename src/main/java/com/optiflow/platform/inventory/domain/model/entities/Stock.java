package com.optiflow.platform.inventory.domain.model.entities;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Quantity of a frame model in an inventory and the level that triggers its alert. */
public class Stock {

  public static final int DEFAULT_MINIMUM_STOCK = 5;

  private final FrameModelId frameModelId;
  private int quantity;
  private int minimumStock;

  public Stock(FrameModelId frameModelId, int quantity, int minimumStock) {
    if (frameModelId == null) {
      throw new DomainException("Frame model id is required.", 400);
    }
    if (quantity < 0) {
      throw new DomainException("Stock quantity cannot be negative.", 400);
    }
    this.frameModelId = frameModelId;
    this.quantity = quantity;
    changeMinimumStock(minimumStock);
  }

  public void increase(int units) {
    quantity += units;
  }

  /** Takes out the units that are available and returns how many were taken. */
  public int decrease(int units) {
    int taken = Math.min(units, quantity);
    quantity -= taken;
    return taken;
  }

  public void changeMinimumStock(int minimumStock) {
    if (minimumStock < 0) {
      throw new DomainException("Minimum stock cannot be negative.", 400);
    }
    this.minimumStock = minimumStock;
  }

  public boolean isLow() {
    return quantity <= minimumStock;
  }

  public FrameModelId frameModelId() {
    return frameModelId;
  }

  public int quantity() {
    return quantity;
  }

  public int minimumStock() {
    return minimumStock;
  }
}
