package com.optiflow.booking.domain.vo;

import com.optiflow.booking.domain.exception.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record StoreRating(BigDecimal value) {

  public StoreRating {
    if (value == null) {
      throw new DomainException("Store rating is required.", 400);
    }
    value = value.setScale(2, RoundingMode.HALF_UP);
    if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("5.00")) > 0) {
      throw new DomainException("Store rating must be between 0 and 5.", 400);
    }
  }

  public static StoreRating of(BigDecimal value) {
    return new StoreRating(value);
  }

  public static StoreRating zero() {
    return new StoreRating(BigDecimal.ZERO);
  }

  public static StoreRating given(BigDecimal value) {
    StoreRating rating = new StoreRating(value);
    if (rating.value().compareTo(BigDecimal.ONE) < 0) {
      throw new DomainException("Store rating must be between 1 and 5.", 400);
    }
    return rating;
  }
}
