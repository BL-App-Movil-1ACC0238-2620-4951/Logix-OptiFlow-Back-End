package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record PhoneNumber(String value) {

  public PhoneNumber {
    String message = "Phone number must contain 7 to 15 digits, with an optional leading +, spaces or hyphens.";
    if (value == null || !value.trim().matches("^\\+?[0-9]+(?:[ -][0-9]+)*$")) {
      throw new DomainException(message, 400);
    }
    String digits = value.trim().replaceAll("[ +\\-]", "");
    if (digits.length() < 7 || digits.length() > 15) {
      throw new DomainException(message, 400);
    }
    value = digits;
  }
}
