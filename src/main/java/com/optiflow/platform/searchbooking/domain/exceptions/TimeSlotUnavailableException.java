package com.optiflow.platform.searchbooking.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class TimeSlotUnavailableException extends DomainException {

  public TimeSlotUnavailableException() {
    super("The selected time slot is no longer available.", 409);
  }
}
