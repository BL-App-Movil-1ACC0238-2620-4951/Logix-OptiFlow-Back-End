package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;

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
