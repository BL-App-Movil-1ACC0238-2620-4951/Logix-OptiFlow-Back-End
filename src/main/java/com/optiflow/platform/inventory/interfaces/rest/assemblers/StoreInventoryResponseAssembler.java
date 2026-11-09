package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.application.results.FrameModelDetails;
import com.optiflow.platform.inventory.application.results.InventoryStock;
import com.optiflow.platform.inventory.application.results.StockLevel;
import com.optiflow.platform.inventory.application.results.StockMovementDetails;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.entities.Stock;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;
import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.interfaces.rest.resources.FrameModelResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.InventoryResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.LowStockAlertResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.StockMovementResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.StockResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.SupplierResponse;
import org.springframework.stereotype.Component;

@Component
public class StoreInventoryResponseAssembler {

  public FrameModelResponse toFrameModelResponse(FrameModel frameModel) {
    return toFrameModelResponse(new FrameModelDetails(frameModel, null));
  }

  public FrameModelResponse toFrameModelResponse(FrameModelDetails details) {
    FrameModel frameModel = details.frameModel();
    Stock stock = details.stock();
    return new FrameModelResponse(
        frameModel.id().value(),
        frameModel.sku().value(),
        frameModel.name(),
        frameModel.material(),
        frameModel.shape(),
        frameModel.color(),
        frameModel.price().amount(),
        frameModel.price().currency(),
        frameModel.tryOnModelUrl(),
        stock == null ? null : stock.quantity(),
        stock == null ? null : stock.minimumStock());
  }

  public InventoryResponse toInventoryResponse(InventoryStock inventoryStock) {
    return new InventoryResponse(
        inventoryStock.inventory().id().value(),
        inventoryStock.inventory().opticalStoreId().value(),
        inventoryStock.levels().stream().map(this::toStockResponse).toList());
  }

  public StockResponse toStockResponse(StockLevel level) {
    return new StockResponse(
        level.frameModel().id().value(),
        level.frameModel().sku().value(),
        level.frameModel().name(),
        level.frameModel().price().amount(),
        level.stock().quantity(),
        level.stock().minimumStock(),
        level.stock().isLow());
  }

  public LowStockAlertResponse toLowStockAlertResponse(StockLevel level) {
    return new LowStockAlertResponse(
        level.frameModel().id().value(),
        level.frameModel().sku().value(),
        level.frameModel().name(),
        level.stock().quantity(),
        level.stock().minimumStock());
  }

  public StockMovementResponse toStockMovementResponse(StockMovementDetails details) {
    StockMovement movement = details.movement();
    return new StockMovementResponse(
        movement.id().value(),
        movement.createdAt(),
        details.frameModel().sku().value(),
        details.frameModel().name(),
        movement.type().name(),
        movement.reason().name(),
        movement.quantity(),
        movement.saleId(),
        movement.supplierId() == null ? null : movement.supplierId().value(),
        movement.performedBy());
  }

  public SupplierResponse toSupplierResponse(Supplier supplier) {
    return new SupplierResponse(supplier.id().value(), supplier.name(), supplier.contact());
  }
}
