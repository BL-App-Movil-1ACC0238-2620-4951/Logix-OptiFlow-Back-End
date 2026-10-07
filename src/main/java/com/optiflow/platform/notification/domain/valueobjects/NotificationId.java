package com.optiflow.platform.notification.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record NotificationId(UUID value) {

  public NotificationId {
    if (value == null) {
      throw new DomainException("Notification id is required.", 400);
    }
  }

  public static NotificationId generate() {
    return new NotificationId(UUID.randomUUID());
  }

  public static NotificationId of(UUID value) {
    return new NotificationId(value);
  }
}
