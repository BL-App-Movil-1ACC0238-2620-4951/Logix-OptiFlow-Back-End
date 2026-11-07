package com.optiflow.platform.production.application.commandhandlers;

import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.commands.MarkOrderAsDeliveredCommand;
import com.optiflow.platform.production.domain.model.events.OrderWasMarkedAsDelivered;
import com.optiflow.platform.production.domain.model.events.WorkOrderStatusUpdated;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkOrderAsDeliveredHandler {

  private final WorkOrderRepository workOrderRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public MarkOrderAsDeliveredHandler(
      WorkOrderRepository workOrderRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(MarkOrderAsDeliveredCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Instant now = clock.instant();
    WorkOrderStatus previousStatus = workOrder.status();
    workOrder.markAsDelivered(now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new WorkOrderStatusUpdated(
        workOrder.id(), workOrder.patientId(), previousStatus, workOrder.status(), now));
    eventPublisher.publish(new OrderWasMarkedAsDelivered(
        workOrder.id(), workOrder.saleId(), workOrder.patientId(), now));
    return workOrder;
  }
}
