package com.optiflow.platform.notification.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record LoyaltyPoints(int value) {

  public static final LoyaltyPoints ZERO = new LoyaltyPoints(0);

  public LoyaltyPoints {
    if (value < 0) {
      throw new DomainException("Loyalty points cannot be negative.", 400);
    }
  }

  public static LoyaltyPoints of(int value) {
    return new LoyaltyPoints(value);
  }

  public LoyaltyPoints add(LoyaltyPoints other) {
    return new LoyaltyPoints(value + other.value);
  }
}
