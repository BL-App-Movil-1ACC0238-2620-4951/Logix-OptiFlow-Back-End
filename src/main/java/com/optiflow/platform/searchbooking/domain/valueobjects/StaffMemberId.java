package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record StaffMemberId(UUID value) {

  public StaffMemberId {
    if (value == null) {
      throw new DomainException("Staff member id is required.", 400);
    }
  }

  public static StaffMemberId generate() {
    return new StaffMemberId(UUID.randomUUID());
  }

  public static StaffMemberId of(UUID value) {
    return new StaffMemberId(value);
  }
}
