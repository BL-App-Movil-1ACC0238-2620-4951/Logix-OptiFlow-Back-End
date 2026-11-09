package com.optiflow.platform.inventory.domain.model.valueobjects;

/** A frame model whose quantity reached its minimum stock. */
public record LowStockAlert(FrameModelId frameModelId, int quantity, int minimumStock) {
}
