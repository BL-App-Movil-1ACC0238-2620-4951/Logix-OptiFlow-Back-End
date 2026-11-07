package com.optiflow.platform.notification.domain.exceptions;

import com.optiflow.platform.shared.domain.exceptions.DomainException;

public class NotificationNotFoundException extends DomainException {

  public NotificationNotFoundException() {
    super("Notification was not found.", 404);
  }
}
