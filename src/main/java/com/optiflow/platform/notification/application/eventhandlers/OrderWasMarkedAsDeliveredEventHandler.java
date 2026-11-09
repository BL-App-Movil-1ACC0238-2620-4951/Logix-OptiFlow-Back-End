package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.SendSatisfactionSurveyHandler;
import com.optiflow.platform.notification.domain.model.commands.SendSatisfactionSurveyCommand;
import com.optiflow.platform.production.domain.model.events.OrderWasMarkedAsDelivered;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/** Sends the satisfaction survey when Production &amp; Tracking delivers an order. */
@Component
public class OrderWasMarkedAsDeliveredEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OrderWasMarkedAsDeliveredEventHandler.class);

  private final SendSatisfactionSurveyHandler sendSatisfactionSurveyHandler;

  public OrderWasMarkedAsDeliveredEventHandler(
      SendSatisfactionSurveyHandler sendSatisfactionSurveyHandler) {
    this.sendSatisfactionSurveyHandler = sendSatisfactionSurveyHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onOrderWasMarkedAsDelivered(OrderWasMarkedAsDelivered event) {
    try {
      sendSatisfactionSurveyHandler.handle(new SendSatisfactionSurveyCommand(
          event.patientId().value(), event.workOrderId().value()));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not send the satisfaction survey for work order {}.",
          event.workOrderId().value(),
          exception);
    }
  }
}
