package com.optiflow.platform.inventory.application.queryservices;

import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.queries.GetSuppliersQuery;
import com.optiflow.platform.inventory.domain.repositories.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierQueryService {

  private final SupplierRepository supplierRepository;

  public SupplierQueryService(SupplierRepository supplierRepository) {
    this.supplierRepository = supplierRepository;
  }

  @Transactional(readOnly = true)
  public List<Supplier> handle(GetSuppliersQuery query) {
    return supplierRepository.findAll();
  }
}
