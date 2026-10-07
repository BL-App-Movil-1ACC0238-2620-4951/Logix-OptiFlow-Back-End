package com.optiflow.platform.clinical.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class SaleNotFoundException extends DomainException {

  public SaleNotFoundException() {
    super("Sale was not found.", 404);
  }
}
