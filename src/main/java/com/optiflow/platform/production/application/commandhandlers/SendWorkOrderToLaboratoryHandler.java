package com.optiflow.platform.production.application.commandhandlers;

import com.optiflow.platform.production.domain.exceptions.LaboratoryNotFoundException;
import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.commands.SendWorkOrderToLaboratoryCommand;
import com.optiflow.platform.production.domain.model.entities.Laboratory;
import com.optiflow.platform.production.domain.model.events.WorkOrderSentToLaboratory;
import com.optiflow.platform.production.domain.model.events.WorkOrderStatusUpdated;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.domain.repositories.LaboratoryRepository;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SendWorkOrderToLaboratoryHandler {

  private final WorkOrderRepository workOrderRepository;
  private final LaboratoryRepository laboratoryRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public SendWorkOrderToLaboratoryHandler(
      WorkOrderRepository workOrderRepository,
      LaboratoryRepository laboratoryRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.laboratoryRepository = laboratoryRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(SendWorkOrderToLaboratoryCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Laboratory laboratory = laboratoryRepository.findById(command.laboratoryId())
        .orElseThrow(LaboratoryNotFoundException::new);
    Instant now = clock.instant();
    WorkOrderStatus previousStatus = workOrder.status();
    workOrder.sendToLaboratory(laboratory, now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new WorkOrderSentToLaboratory(workOrder.id(), laboratory.id(), now));
    if (workOrder.status() != previousStatus) {
      eventPublisher.publish(new WorkOrderStatusUpdated(
          workOrder.id(), workOrder.patientId(), previousStatus, workOrder.status(), now));
    }
    return workOrder;
  }
}
