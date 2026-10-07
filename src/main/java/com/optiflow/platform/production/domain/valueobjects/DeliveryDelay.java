package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.time.LocalDate;

/** A reported delay of a work order and its new estimated delivery date. */
public record DeliveryDelay(String reason, Instant reportedAt, LocalDate newEstimatedDeliveryDate) {

  public DeliveryDelay {
    if (reason == null || reason.isBlank()) {
      throw new DomainException("A delay reason is required.", 400);
    }
    reason = reason.trim();
    if (reason.length() > 255) {
      throw new DomainException("Delay reason must have at most 255 characters.", 400);
    }
    if (reportedAt == null) {
      throw new DomainException("Delay report date is required.", 400);
    }
    if (newEstimatedDeliveryDate == null) {
      throw new DomainException("New estimated delivery date is required.", 400);
    }
  }
}
