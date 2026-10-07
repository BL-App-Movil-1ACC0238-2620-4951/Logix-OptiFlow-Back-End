package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Discount(DiscountType type, BigDecimal value, String reason) {

  private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

  public Discount {
    if (type == null) {
      throw new DomainException("Discount type is required.", 400);
    }
    if (value == null || value.signum() <= 0) {
      throw new DomainException("Discount value must be greater than zero.", 400);
    }
    if (type == DiscountType.PERCENTAGE && value.compareTo(ONE_HUNDRED) > 0) {
      throw new DomainException("A percentage discount cannot exceed 100.", 400);
    }
    value = value.setScale(2, RoundingMode.HALF_UP);
    reason = reason == null || reason.isBlank() ? null : reason.trim();
    if (reason != null && reason.length() > 255) {
      throw new DomainException("Discount reason must have at most 255 characters.", 400);
    }
  }

  public Money amountFor(Money subtotal) {
    Money amount = type == DiscountType.PERCENTAGE
        ? Money.of(subtotal.amount().multiply(value).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP))
        : Money.of(value);
    if (amount.isGreaterThan(subtotal)) {
      throw new DomainException("The discount cannot exceed the quotation subtotal.", 400);
    }
    return amount;
  }
}
