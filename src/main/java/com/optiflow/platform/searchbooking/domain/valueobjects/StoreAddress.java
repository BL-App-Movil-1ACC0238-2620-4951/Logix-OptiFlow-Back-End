package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public record StoreAddress(String value) {

  public StoreAddress {
    if (value == null || value.trim().length() < 5 || value.trim().length() > 200) {
      throw new DomainException("Optical store address is invalid.", 400);
    }
    value = value.trim();
  }
}
