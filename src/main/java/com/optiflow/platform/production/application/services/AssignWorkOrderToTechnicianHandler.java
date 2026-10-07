package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.commands.AssignWorkOrderToTechnicianCommand;
import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.events.WorkOrderAssigned;
import com.optiflow.platform.production.domain.exceptions.TechnicianNotFoundException;
import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.repositories.TechnicianRepository;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignWorkOrderToTechnicianHandler {

  private final WorkOrderRepository workOrderRepository;
  private final TechnicianRepository technicianRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public AssignWorkOrderToTechnicianHandler(
      WorkOrderRepository workOrderRepository,
      TechnicianRepository technicianRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.technicianRepository = technicianRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(AssignWorkOrderToTechnicianCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Technician technician = technicianRepository.findById(command.technicianId())
        .orElseThrow(TechnicianNotFoundException::new);
    Instant now = clock.instant();
    workOrder.assignTo(technician, now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new WorkOrderAssigned(workOrder.id(), technician.id(), now));
    return workOrder;
  }
}
