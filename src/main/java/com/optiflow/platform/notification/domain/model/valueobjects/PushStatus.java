package com.optiflow.platform.notification.domain.model.valueobjects;

/** Result of sending a notification through Firebase Cloud Messaging. */
public enum PushStatus {
  PENDING,
  SENT,
  FAILED
}
