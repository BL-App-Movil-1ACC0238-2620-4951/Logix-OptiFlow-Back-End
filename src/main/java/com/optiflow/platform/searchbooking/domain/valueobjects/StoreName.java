package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record StoreName(String value) {

  public StoreName {
    if (value == null || value.trim().length() < 2 || value.trim().length() > 120) {
      throw new DomainException("Optical store name is invalid.", 400);
    }
    value = value.trim();
  }
}
