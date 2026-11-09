package com.optiflow.platform.notification.application.outboundservices;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import java.util.List;

/** Port to the push notification provider (Firebase Cloud Messaging). */
public interface PushNotificationService {

  /** Sends the notification to each device of the patient. */
  PushDelivery send(Notification notification, List<DeviceToken> devices);
}
