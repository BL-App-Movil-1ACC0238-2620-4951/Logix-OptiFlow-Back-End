package com.optiflow.platform.inventory.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class InventoryNotFoundException extends DomainException {

  public InventoryNotFoundException() {
    super("This optical store has no inventory.", 404);
  }
}
