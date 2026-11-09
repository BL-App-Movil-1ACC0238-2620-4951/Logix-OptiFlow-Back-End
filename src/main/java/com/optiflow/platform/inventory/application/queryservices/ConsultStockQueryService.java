package com.optiflow.platform.inventory.application.queryservices;

import com.optiflow.platform.inventory.application.results.FrameModelDetails;
import com.optiflow.platform.inventory.application.results.InventoryStock;
import com.optiflow.platform.inventory.application.results.StockLevel;
import com.optiflow.platform.inventory.domain.exceptions.FrameModelNotFoundException;
import com.optiflow.platform.inventory.domain.exceptions.InventoryNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.entities.Stock;
import com.optiflow.platform.inventory.domain.model.queries.ConsultStockQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameModelBySkuQuery;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.domain.repositories.InventoryRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Stock of a store and the data of a scanned frame model. */
@Service
public class ConsultStockQueryService {

  private final InventoryRepository inventoryRepository;
  private final FrameModelRepository frameModelRepository;

  public ConsultStockQueryService(
      InventoryRepository inventoryRepository, FrameModelRepository frameModelRepository) {
    this.inventoryRepository = inventoryRepository;
    this.frameModelRepository = frameModelRepository;
  }

  @Transactional(readOnly = true)
  public InventoryStock handle(ConsultStockQuery query) {
    Inventory inventory = inventoryRepository.findByOpticalStoreId(query.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    return new InventoryStock(inventory, levelsOf(inventory, inventory.stocks()));
  }

  @Transactional(readOnly = true)
  public FrameModelDetails handle(GetFrameModelBySkuQuery query) {
    FrameModel frameModel = frameModelRepository.findBySku(query.sku())
        .orElseThrow(FrameModelNotFoundException::new);
    if (query.opticalStoreId() == null) {
      return new FrameModelDetails(frameModel, null);
    }
    Inventory inventory = inventoryRepository.findByOpticalStoreId(query.opticalStoreId())
        .orElseThrow(InventoryNotFoundException::new);
    return new FrameModelDetails(frameModel, inventory.stockOf(frameModel.id()).orElse(null));
  }

  List<StockLevel> levelsOf(Inventory inventory, List<Stock> stocks) {
    return stocks.stream()
        .flatMap(stock -> frameModelRepository.findById(stock.frameModelId()).stream()
            .map(frameModel -> new StockLevel(frameModel, stock)))
        .sorted(Comparator.comparing(level -> level.frameModel().name()))
        .toList();
  }
}
