package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record PhoneNumber(String value) {

  public PhoneNumber {
    if (value == null) {
      throw new DomainException("Phone number is invalid.", 400);
    }
    String digits = value.replaceAll("\\D", "");
    if (digits.length() < 7 || digits.length() > 15) {
      throw new DomainException("Phone number is invalid.", 400);
    }
    value = digits;
  }
}
