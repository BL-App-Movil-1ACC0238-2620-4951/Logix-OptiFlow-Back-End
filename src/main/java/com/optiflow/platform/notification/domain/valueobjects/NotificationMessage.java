package com.optiflow.platform.notification.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;

public record NotificationMessage(String title, String body) {

  public NotificationMessage {
    if (title == null || title.isBlank()) {
      throw new DomainException("Notification title is required.", 400);
    }
    if (body == null || body.isBlank()) {
      throw new DomainException("Notification body is required.", 400);
    }
  }
}
