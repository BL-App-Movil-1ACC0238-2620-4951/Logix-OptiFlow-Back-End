package com.optiflow.platform.inventory.domain.repositories;

import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {

  void save(Supplier supplier);

  Optional<Supplier> findById(SupplierId id);

  List<Supplier> findAll();
}
