package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record Name(String value) {

  public Name {
    if (value == null || !value.trim().matches("^[\\p{L}][\\p{L} .'-]{1,79}$")) {
      throw new DomainException("Patient name is invalid.", 400);
    }
    value = value.trim();
  }
}
