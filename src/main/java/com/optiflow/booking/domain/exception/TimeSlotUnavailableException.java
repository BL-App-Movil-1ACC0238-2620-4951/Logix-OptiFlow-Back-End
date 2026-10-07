package com.optiflow.booking.domain.exception;

public class TimeSlotUnavailableException extends DomainException {

  public TimeSlotUnavailableException() {
    super("The selected time slot is no longer available.", 409);
  }
}
