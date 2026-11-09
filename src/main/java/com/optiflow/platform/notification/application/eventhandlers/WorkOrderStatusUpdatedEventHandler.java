package com.optiflow.platform.notification.application.eventhandlers;

import com.optiflow.platform.notification.application.commandhandlers.NotifyLensOrderProgressHandler;
import com.optiflow.platform.notification.domain.model.commands.NotifyLensOrderProgressCommand;
import com.optiflow.platform.production.domain.model.events.WorkOrderStatusUpdated;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
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
      case IN_WORKSHOP -> "Tus lentes se están preparando en el taller.";
      case QUALITY_CONTROL -> "Tus lentes están en control de calidad.";
      case READY_FOR_DELIVERY -> "Tu pedido está listo para recoger en la óptica.";
      case DELIVERED -> "Tu pedido fue entregado.";
      case PENDING -> "Tu pedido está pendiente.";
    };
  }
}
