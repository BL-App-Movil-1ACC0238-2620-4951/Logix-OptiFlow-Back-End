package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commands.NotifyLensOrderProgressCommand;
import com.optiflow.platform.notification.application.services.NotifyLensOrderProgressHandler;
import com.optiflow.platform.production.domain.events.WorkOrderStatusUpdated;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WorkOrderStatusUpdatedEventHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(WorkOrderStatusUpdatedEventHandler.class);

  private final NotifyLensOrderProgressHandler notifyLensOrderProgressHandler;

  public WorkOrderStatusUpdatedEventHandler(
      NotifyLensOrderProgressHandler notifyLensOrderProgressHandler) {
    this.notifyLensOrderProgressHandler = notifyLensOrderProgressHandler;
  }

  @TransactionalEventListener(fallbackExecution = true)
  public void onWorkOrderStatusUpdated(WorkOrderStatusUpdated event) {
    if (event.status() == WorkOrderStatus.PENDING) {
      return;
    }
    try {
      notifyLensOrderProgressHandler.handle(new NotifyLensOrderProgressCommand(
          event.patientId().value(),
          event.workOrderId().value(),
          progressMessage(event.status())));
    } catch (RuntimeException exception) {
      LOGGER.error(
          "Could not notify lens order progress for work order {}.",
          event.workOrderId().value(),
          exception);
    }
  }

  private static String progressMessage(WorkOrderStatus status) {
    return switch (status) {
      case IN_WORKSHOP -> "Your lenses are being prepared in the workshop.";
      case QUALITY_CONTROL -> "Your lenses are in quality control.";
      case READY_FOR_DELIVERY -> "Your order is ready for delivery.";
      case DELIVERED -> "Your order was delivered.";
      case PENDING -> "Your order is pending.";
    };
  }
}
