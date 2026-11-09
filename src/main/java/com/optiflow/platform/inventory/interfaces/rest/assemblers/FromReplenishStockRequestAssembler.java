package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.commands.ReplenishStockCommand;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.inventory.interfaces.rest.resources.ReplenishStockRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromReplenishStockRequestAssembler {

  public ReplenishStockCommand toCommand(ReplenishStockRequest request, UUID performedBy) {
    return new ReplenishStockCommand(
        OpticalStoreId.of(request.opticalStoreId()),
        Sku.of(request.sku()),
        SupplierId.of(request.supplierId()),
        request.quantity(),
        request.minimumStock(),
        performedBy);
  }
}
