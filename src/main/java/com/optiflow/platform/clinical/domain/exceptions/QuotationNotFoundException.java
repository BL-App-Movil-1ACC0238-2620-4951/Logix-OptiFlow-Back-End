package com.optiflow.platform.clinical.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class QuotationNotFoundException extends DomainException {

  public QuotationNotFoundException() {
    super("Quotation was not found.", 404);
  }
}
