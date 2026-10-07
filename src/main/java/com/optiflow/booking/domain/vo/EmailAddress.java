package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;

public record EmailAddress(String value) {

  public EmailAddress {
    if (value == null || !value.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
      throw new DomainException("Email address is invalid.", 400);
    }
    value = value.trim().toLowerCase();
  }
}
