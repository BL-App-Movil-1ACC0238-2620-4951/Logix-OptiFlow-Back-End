package com.optiflow.platform.inventory.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class SupplierNotFoundException extends DomainException {

  public SupplierNotFoundException() {
    super("Supplier was not found.", 404);
  }
}
