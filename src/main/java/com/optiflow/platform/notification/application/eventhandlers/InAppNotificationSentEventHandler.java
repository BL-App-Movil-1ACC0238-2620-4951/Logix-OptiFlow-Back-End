package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.SendPushNotificationHandler;
import com.optiflow.platform.notification.domain.model.commands.SendPushNotificationCommand;
import com.optiflow.platform.notification.domain.model.events.InAppNotificationSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/** Sends each new notification as a push notification once it is saved. */
@Component
public class InAppNotificationSentEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InAppNotificationSentEventHandler.class);

  private final SendPushNotificationHandler sendPushNotificationHandler;

  public InAppNotificationSentEventHandler(
      SendPushNotificationHandler sendPushNotificationHandler) {
    this.sendPushNotificationHandler = sendPushNotificationHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onInAppNotificationSent(InAppNotificationSent event) {
    try {
      sendPushNotificationHandler.handle(
          new SendPushNotificationCommand(event.notificationId().value()));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not send the push notification {}.", event.notificationId().value(), exception);
    }
  }
}
