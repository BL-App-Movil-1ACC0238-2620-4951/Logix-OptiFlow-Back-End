package com.optiflow.platform.production.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class TechnicianNotFoundException extends DomainException {

  public TechnicianNotFoundException() {
    super("Technician was not found.", 404);
  }
}
