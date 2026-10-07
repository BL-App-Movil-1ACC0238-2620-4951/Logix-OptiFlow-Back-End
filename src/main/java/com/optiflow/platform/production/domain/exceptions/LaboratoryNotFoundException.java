package com.optiflow.platform.production.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class LaboratoryNotFoundException extends DomainException {

  public LaboratoryNotFoundException() {
    super("Laboratory was not found.", 404);
  }
}
