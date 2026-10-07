package com.optiflow.platform.notification.domain.exceptions;

import com.optiflow.platform.shared.exceptions.DomainException;

public class LoyaltyAccountNotFoundException extends DomainException {

  public LoyaltyAccountNotFoundException() {
    super("Loyalty account was not found.", 404);
  }
}
