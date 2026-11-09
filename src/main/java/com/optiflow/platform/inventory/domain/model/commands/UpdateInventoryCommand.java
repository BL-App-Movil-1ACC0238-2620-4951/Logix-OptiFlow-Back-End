package com.optiflow.platform.inventory.domain.model.commands;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.SoldProduct;
import java.util.List;
import java.util.UUID;

public record UpdateInventoryCommand(OpticalStoreId opticalStoreId, UUID saleId, List<SoldProduct> soldProducts) {
}
