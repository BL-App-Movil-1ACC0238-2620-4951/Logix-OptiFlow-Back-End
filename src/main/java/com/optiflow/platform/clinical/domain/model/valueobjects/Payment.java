package com.optiflow.platform.clinical.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;

/**
 * Payment of a sale, approved or rejected by the payment gateway or made in cash.
 *
 * <p>{@code statusDetail} is the reason given by the gateway (for example
 * {@code cc_rejected_insufficient_amount}); it is empty for cash payments.
 */
public record Payment(
    PaymentMethod method,
    Money amount,
    String transactionReference,
    PaymentStatus status,
    String statusDetail,
    Instant paidAt) {

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
    if (status == null) {
      throw new DomainException("Payment status is required.", 400);
    }
    if (statusDetail != null && statusDetail.length() > 100) {
      throw new DomainException("Payment status detail must have at most 100 characters.", 400);
    }
    if (paidAt == null) {
      throw new DomainException("Payment date is required.", 400);
    }
  }

  public static Payment approved(
      PaymentMethod method, Money amount, String transactionReference, Instant paidAt) {
    return new Payment(
        method, amount, transactionReference, PaymentStatus.APPROVED, null, paidAt);
  }

  public boolean isApproved() {
    return status == PaymentStatus.APPROVED;
  }
}
