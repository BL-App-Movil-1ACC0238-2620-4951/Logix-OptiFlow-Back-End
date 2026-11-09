package com.optiflow.platform.inventory.application.services;

import com.optiflow.platform.inventory.application.commandhandlers.RegisterSupplierHandler;
import com.optiflow.platform.inventory.application.queryservices.SupplierQueryService;
import com.optiflow.platform.inventory.domain.model.commands.RegisterSupplierCommand;
import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.queries.GetSuppliersQuery;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SupplierApplicationService {

  private final RegisterSupplierHandler registerSupplierHandler;
  private final SupplierQueryService supplierQueryService;

  public SupplierApplicationService(
      RegisterSupplierHandler registerSupplierHandler, SupplierQueryService supplierQueryService) {
    this.registerSupplierHandler = registerSupplierHandler;
    this.supplierQueryService = supplierQueryService;
  }

  public Supplier register(RegisterSupplierCommand command) {
    return registerSupplierHandler.handle(command);
  }

  public List<Supplier> suppliers(GetSuppliersQuery query) {
    return supplierQueryService.handle(query);
  }
}
