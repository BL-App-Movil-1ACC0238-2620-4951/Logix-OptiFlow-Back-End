package com.optiflow.platform.inventory.application.results;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import java.time.LocalDate;
import java.util.List;

/** Units in and out of a store in a period, and the current stock of each frame model. */
public record InventoryMetrics(
    OpticalStoreId opticalStoreId,
    LocalDate from,
    LocalDate to,
    List<ProductMetric> products) {

  public int unitsIn() {
    return products.stream().mapToInt(ProductMetric::unitsIn).sum();
  }

  public int unitsOut() {
    return products.stream().mapToInt(ProductMetric::unitsOut).sum();
  }

  public long lowStockProducts() {
    return products.stream().filter(product -> product.level().stock().isLow()).count();
  }

  public record ProductMetric(StockLevel level, int unitsIn, int unitsOut) {
  }
}
