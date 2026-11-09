package com.optiflow.platform.inventory.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class FrameModelNotFoundException extends DomainException {

  public FrameModelNotFoundException() {
    super("Frame model was not found.", 404);
  }
}
