package com.optiflow.platform.inventory.application.queryservices;

import com.optiflow.platform.inventory.application.results.InventoryMetrics;
import com.optiflow.platform.inventory.application.results.InventoryMetrics.ProductMetric;
import com.optiflow.platform.inventory.application.results.StockLevel;
import com.optiflow.platform.inventory.domain.exceptions.InventoryNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.queries.GetInventoryMetricsQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementType;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import com.optiflow.platform.inventory.domain.repositories.StockMovementRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inventory metrics of a store for the admin reports, in JSON or exported as CSV. */
@Service
public class InventoryMetricsQueryService {

  private static final String CSV_HEADER =
      "sku,name,quantity,minimum_stock,low_stock,units_in,units_out";

  private final InventoryRepository inventoryRepository;
  private final StockMovementRepository stockMovementRepository;
  private final ConsultStockQueryService consultStockQueryService;
  private final Clock clock;

  public InventoryMetricsQueryService(
      InventoryRepository inventoryRepository,
      StockMovementRepository stockMovementRepository,
      ConsultStockQueryService consultStockQueryService,
      Clock clock) {
    this.inventoryRepository = inventoryRepository;
    this.stockMovementRepository = stockMovementRepository;
    this.consultStockQueryService = consultStockQueryService;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public InventoryMetrics handle(GetInventoryMetricsQuery query) {
    if (query.from().isAfter(query.to())) {
      throw new DomainException("The start date must not be after the end date.", 400);
    }
    Inventory inventory = inventoryRepository.findByOpticalStoreId(query.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    Instant from = query.from().atStartOfDay(clock.getZone()).toInstant();
    Instant to = query.to().plusDays(1).atStartOfDay(clock.getZone()).toInstant();
    List<StockMovement> movements =
        stockMovementRepository.findByPeriod(inventory.id(), from, to, null);
    Map<FrameModelId, Integer> unitsIn = unitsByFrame(movements, MovementType.IN);
    Map<FrameModelId, Integer> unitsOut = unitsByFrame(movements, MovementType.OUT);
    List<ProductMetric> products = consultStockQueryService
        .levelsOf(inventory, inventory.stocks()).stream()
        .map(level -> new ProductMetric(
            level,
            unitsIn.getOrDefault(level.frameModel().id(), 0),
            unitsOut.getOrDefault(level.frameModel().id(), 0)))
        .toList();
    return new InventoryMetrics(query.opticalStoreId(), query.from(), query.to(), products);
  }

  /** One row per frame model of the store. */
  public String toCsv(InventoryMetrics metrics) {
    StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
    for (ProductMetric product : metrics.products()) {
      StockLevel level = product.level();
      csv.append(String.join(",",
              escape(level.frameModel().sku().value()),
              escape(level.frameModel().name()),
              String.valueOf(level.stock().quantity()),
              String.valueOf(level.stock().minimumStock()),
              String.valueOf(level.stock().isLow()),
              String.valueOf(product.unitsIn()),
              String.valueOf(product.unitsOut())))
          .append('\n');
    }
    return csv.toString();
  }

  private static Map<FrameModelId, Integer> unitsByFrame(
      List<StockMovement> movements, MovementType type) {
    return movements.stream()
        .filter(movement -> movement.type() == type)
        .collect(Collectors.groupingBy(
            StockMovement::frameModelId, Collectors.summingInt(StockMovement::quantity)));
  }

  private static String escape(String value) {
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}
