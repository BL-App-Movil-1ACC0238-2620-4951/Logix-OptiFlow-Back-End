package com.optiflow.platform.notification.infrastructure.outboundservices;

import com.optiflow.platform.notification.application.outboundservices.PushDelivery;
import com.optiflow.platform.notification.application.outboundservices.PushNotificationService;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Translates OptiFlow notifications into Firebase Cloud Messaging messages, so the notification
 * model does not depend on the format of the provider.
 */
@Component
public class MessagingAntiCorruptionLayer implements PushNotificationService {

  private final FirebaseMessagingAdapter firebaseMessagingAdapter;

  public MessagingAntiCorruptionLayer(FirebaseMessagingAdapter firebaseMessagingAdapter) {
    this.firebaseMessagingAdapter = firebaseMessagingAdapter;
  }

  @Override
  public PushDelivery send(Notification notification, List<DeviceToken> devices) {
    if (!firebaseMessagingAdapter.isConfigured()) {
      return PushDelivery.NONE;
    }
    FcmMessage message = new FcmMessage(
        notification.message().title(),
        notification.message().body(),
        Map.of(
            "notificationId", notification.id().value().toString(),
            "type", notification.type().name()));
    int delivered = 0;
    List<String> invalidTokens = new ArrayList<>();
    for (DeviceToken device : devices) {
      switch (firebaseMessagingAdapter.send(device.token(), message)) {
        case SENT -> delivered++;
        case INVALID_TOKEN -> invalidTokens.add(device.token());
        case FAILED -> { }
      }
    }
    return new PushDelivery(delivered, invalidTokens);
  }
}
