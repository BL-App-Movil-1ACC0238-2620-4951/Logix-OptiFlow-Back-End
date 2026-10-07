package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;

public record Payment(
    PaymentMethod method, Money amount, String transactionReference, Instant paidAt) {

  public Payment {
    if (method == null) {
      throw new DomainException("Payment method is required.", 400);
    }
    if (amount == null || amount.isZero()) {
      throw new DomainException("Payment amount must be greater than zero.", 400);
    }
    if (transactionReference == null || transactionReference.isBlank()) {
      throw new DomainException("Transaction reference is required.", 400);
    }
    if (transactionReference.length() > 255) {
      throw new DomainException("Transaction reference must have at most 255 characters.", 400);
    }
    if (paidAt == null) {
      throw new DomainException("Payment date is required.", 400);
    }
  }
}
