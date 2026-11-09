package com.optiflow.platform.inventory.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;

public record FrameModelId(UUID value) {

  public FrameModelId {
    if (value == null) {
      throw new DomainException("Frame model id is required.", 400);
    }
  }

  public static FrameModelId generate() {
    return new FrameModelId(UUID.randomUUID());
  }

  public static FrameModelId of(UUID value) {
    return new FrameModelId(value);
  }
}
