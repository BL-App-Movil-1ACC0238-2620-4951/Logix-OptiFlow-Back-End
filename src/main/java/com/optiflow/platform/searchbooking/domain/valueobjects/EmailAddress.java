package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record EmailAddress(String value) {

  public EmailAddress {
    if (value == null || !value.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw new DomainException("Email address is invalid.", 400);
    }
    value = value.trim().toLowerCase();
  }
}
