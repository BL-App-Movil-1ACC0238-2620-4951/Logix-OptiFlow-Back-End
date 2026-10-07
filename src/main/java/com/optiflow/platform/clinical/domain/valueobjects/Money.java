package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

  public static final String CURRENCY = "PEN";
  public static final Money ZERO = new Money(BigDecimal.ZERO);

  public Money {
    if (amount == null) {
      throw new DomainException("Amount is required.", 400);
    }
    if (amount.signum() < 0) {
      throw new DomainException("Amount cannot be negative.", 400);
    }
    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  public static Money of(BigDecimal amount) {
    return new Money(amount);
  }

  public Money add(Money other) {
    return new Money(amount.add(other.amount));
  }

  public Money subtract(Money other) {
    return new Money(amount.subtract(other.amount));
  }

  public Money multiply(int quantity) {
    return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
  }

  public boolean isGreaterThan(Money other) {
    return amount.compareTo(other.amount) > 0;
  }

  public boolean isZero() {
    return amount.signum() == 0;
  }

  @Override
  public String toString() {
    return "S/ " + amount.toPlainString();
  }
}
