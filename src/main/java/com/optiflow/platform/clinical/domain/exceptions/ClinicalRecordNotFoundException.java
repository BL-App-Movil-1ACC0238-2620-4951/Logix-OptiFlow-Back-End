package com.optiflow.platform.clinical.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class ClinicalRecordNotFoundException extends DomainException {

  public ClinicalRecordNotFoundException() {
    super("Clinical record was not found.", 404);
  }
}
