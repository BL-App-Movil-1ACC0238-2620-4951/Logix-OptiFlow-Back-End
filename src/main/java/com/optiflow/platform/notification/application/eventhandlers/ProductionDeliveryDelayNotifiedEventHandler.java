package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commands.SendDeliveryDelayNotificationCommand;
import com.optiflow.platform.notification.application.services.SendDeliveryDelayNotificationHandler;
import com.optiflow.platform.production.domain.events.DeliveryDelayNotified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductionDeliveryDelayNotifiedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProductionDeliveryDelayNotifiedEventHandler.class);

  private final SendDeliveryDelayNotificationHandler sendDeliveryDelayNotificationHandler;

  public ProductionDeliveryDelayNotifiedEventHandler(
      SendDeliveryDelayNotificationHandler sendDeliveryDelayNotificationHandler) {
    this.sendDeliveryDelayNotificationHandler = sendDeliveryDelayNotificationHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onDeliveryDelayNotified(DeliveryDelayNotified event) {
    try {
      sendDeliveryDelayNotificationHandler.handle(new SendDeliveryDelayNotificationCommand(
          event.patientId().value(),
          event.workOrderId().value(),
          event.delay().reason()));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not send the delivery delay notification for work order {}.",
          event.workOrderId().value(),
          exception);
    }
  }
}
