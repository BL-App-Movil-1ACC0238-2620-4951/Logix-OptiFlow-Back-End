package com.optiflow.platform.inventory.domain.repositories;

import com.optiflow.platform.inventory.domain.model.aggregates.Inventory;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import java.util.Optional;

public interface InventoryRepository {

  void save(Inventory inventory);

  Optional<Inventory> findByOpticalStoreId(OpticalStoreId opticalStoreId);
}
