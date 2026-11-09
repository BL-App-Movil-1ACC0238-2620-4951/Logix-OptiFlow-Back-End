package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InventoryMetricsResponse(
    UUID opticalStoreId,
    LocalDate from,
    LocalDate to,
    int unitsIn,
    int unitsOut,
    long lowStockProducts,
    List<ProductMetricResponse> products) {

  public record ProductMetricResponse(
      String sku,
      String name,
      int quantity,
      int minimumStock,
      boolean lowStock,
      int unitsIn,
      int unitsOut) {
  }
}
