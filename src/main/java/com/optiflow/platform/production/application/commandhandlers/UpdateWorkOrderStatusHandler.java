package com.optiflow.platform.production.application.commandhandlers;

import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.commands.UpdateWorkOrderStatusCommand;
import com.optiflow.platform.production.domain.model.events.WorkOrderStatusUpdated;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateWorkOrderStatusHandler {

  private final WorkOrderRepository workOrderRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public UpdateWorkOrderStatusHandler(
      WorkOrderRepository workOrderRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(UpdateWorkOrderStatusCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Instant now = clock.instant();
    WorkOrderStatus previousStatus = workOrder.status();
    workOrder.updateStatus(command.status(), now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new WorkOrderStatusUpdated(
        workOrder.id(), workOrder.patientId(), previousStatus, workOrder.status(), now));
    return workOrder;
  }
}
