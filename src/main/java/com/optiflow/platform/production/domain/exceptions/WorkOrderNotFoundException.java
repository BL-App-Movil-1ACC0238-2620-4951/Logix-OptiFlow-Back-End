package com.optiflow.platform.production.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class WorkOrderNotFoundException extends DomainException {

  public WorkOrderNotFoundException() {
    super("Work order was not found.", 404);
  }
}
