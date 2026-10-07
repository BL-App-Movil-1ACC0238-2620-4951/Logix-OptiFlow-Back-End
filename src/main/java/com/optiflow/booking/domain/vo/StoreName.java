package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;

public record StoreName(String value) {

  public StoreName {
    if (value == null || value.trim().length() < 2 || value.trim().length() > 120) {
      throw new DomainException("Optical store name is invalid.", 400);
    }
    value = value.trim();
  }
}
