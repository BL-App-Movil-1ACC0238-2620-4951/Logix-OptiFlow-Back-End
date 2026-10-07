package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record WorkOrderId(UUID value) {

  public WorkOrderId {
    if (value == null) {
      throw new DomainException("Work order id is required.", 400);
    }
  }

  public static WorkOrderId generate() {
    return new WorkOrderId(UUID.randomUUID());
  }

  public static WorkOrderId of(UUID value) {
    return new WorkOrderId(value);
  }
}
