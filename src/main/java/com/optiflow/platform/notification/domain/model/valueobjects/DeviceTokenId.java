package com.optiflow.platform.notification.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;

public record DeviceTokenId(UUID value) {

  public DeviceTokenId {
    if (value == null) {
      throw new DomainException("Device token id is required.", 400);
    }
  }

  public static DeviceTokenId generate() {
    return new DeviceTokenId(UUID.randomUUID());
  }

  public static DeviceTokenId of(UUID value) {
    return new DeviceTokenId(value);
  }
}
