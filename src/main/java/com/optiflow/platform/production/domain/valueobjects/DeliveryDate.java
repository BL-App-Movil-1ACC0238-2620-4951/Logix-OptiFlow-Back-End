package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.LocalDate;

/** Estimated date on which the finished order will be delivered to the patient. */
public record DeliveryDate(LocalDate date) {

  /** Standard manufacturing time for a work order. */
  public static final int STANDARD_PRODUCTION_DAYS = 7;

  public DeliveryDate {
    if (date == null) {
      throw new DomainException("Estimated delivery date is required.", 400);
    }
  }

  public static DeliveryDate estimateFrom(LocalDate startDate) {
    return new DeliveryDate(startDate.plusDays(STANDARD_PRODUCTION_DAYS));
  }

  public boolean isOverdue(LocalDate today) {
    return today.isAfter(date);
  }
}
