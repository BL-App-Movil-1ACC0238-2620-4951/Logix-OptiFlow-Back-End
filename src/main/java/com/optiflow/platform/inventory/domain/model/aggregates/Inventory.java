package com.optiflow.platform.inventory.domain.model.aggregates;

import com.optiflow.platform.inventory.domain.model.entities.Stock;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.InventoryId;
import com.optiflow.platform.inventory.domain.model.valueobjects.LowStockAlert;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementReason;
import com.optiflow.platform.inventory.domain.model.valueobjects.MovementType;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Replenishment;
import com.optiflow.platform.inventory.domain.model.valueobjects.StockMovementId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Physical stock of the frame models of one optical store. */
public class Inventory {

  private final InventoryId id;
  private final OpticalStoreId opticalStoreId;
  private final List<Stock> stocks;

  private Inventory(InventoryId id, OpticalStoreId opticalStoreId, List<Stock> stocks) {
    if (id == null || opticalStoreId == null) {
      throw new DomainException("Inventory id and optical store are required.", 400);
    }
    this.id = id;
    this.opticalStoreId = opticalStoreId;
    this.stocks = new ArrayList<>(stocks);
  }

  public static Inventory open(OpticalStoreId opticalStoreId) {
    return new Inventory(InventoryId.generate(), opticalStoreId, List.of());
  }

  public static Inventory reconstitute(
      InventoryId id, OpticalStoreId opticalStoreId, List<Stock> stocks) {
    return new Inventory(id, opticalStoreId, stocks);
  }

  /**
   * Adds the delivered units. The first replenishment of a frame model creates its stock with the
   * given minimum (or {@link Stock#DEFAULT_MINIMUM_STOCK}); later ones may change it.
   */
  public StockMovement replenish(
      Replenishment replenishment, Integer minimumStock, UUID performedBy, Instant now) {
    Stock stock = stockOf(replenishment.frameModelId()).orElseGet(() -> {
      Stock created = new Stock(replenishment.frameModelId(), 0,
          minimumStock == null ? Stock.DEFAULT_MINIMUM_STOCK : minimumStock);
      stocks.add(created);
      return created;
    });
    if (minimumStock != null) {
      stock.changeMinimumStock(minimumStock);
    }
    stock.increase(replenishment.quantity());
    return new StockMovement(StockMovementId.generate(), id, replenishment.frameModelId(),
        MovementType.IN, MovementReason.REPLENISHMENT, replenishment.quantity(), null,
        replenishment.supplierId(), performedBy, now);
  }

  /**
   * Takes out the units of a sale. A frame model without stock in this inventory is not
   * controlled here, and only the available units can be taken out.
   */
  public Optional<StockMovement> decrease(
      FrameModelId frameModelId, int quantity, UUID saleId, Instant now) {
    return stockOf(frameModelId)
        .map(stock -> stock.decrease(quantity))
        .filter(taken -> taken > 0)
        .map(taken -> new StockMovement(StockMovementId.generate(), id, frameModelId,
            MovementType.OUT, MovementReason.SALE, taken, saleId, null, null, now));
  }

  public List<LowStockAlert> lowStockAlerts() {
    return stocks.stream()
        .filter(Stock::isLow)
        .map(stock -> new LowStockAlert(
            stock.frameModelId(), stock.quantity(), stock.minimumStock()))
        .toList();
  }

  public Optional<Stock> stockOf(FrameModelId frameModelId) {
    return stocks.stream().filter(stock -> stock.frameModelId().equals(frameModelId)).findFirst();
  }

  public InventoryId id() {
    return id;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public List<Stock> stocks() {
    return List.copyOf(stocks);
  }
}
