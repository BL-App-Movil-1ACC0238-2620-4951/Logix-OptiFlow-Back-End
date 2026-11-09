package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(BigDecimal amount, String currency) {

  public static final String DEFAULT_CURRENCY = "PEN";

  public Price {
    if (amount == null || amount.signum() <= 0) {
      throw new DomainException("Price must be greater than zero.", 400);
    }
    if (amount.compareTo(new BigDecimal("99999999.99")) > 0) {
      throw new DomainException("Price is too high.", 400);
    }
    amount = amount.setScale(2, RoundingMode.HALF_UP);
    currency = currency == null || currency.isBlank() ? DEFAULT_CURRENCY : currency.trim();
  }

  public static Price of(BigDecimal amount) {
    return new Price(amount, DEFAULT_CURRENCY);
  }
}
