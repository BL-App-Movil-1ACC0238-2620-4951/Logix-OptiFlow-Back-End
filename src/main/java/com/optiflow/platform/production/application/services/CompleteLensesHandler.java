package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.commands.CompleteLensesCommand;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.events.LensesWereCompleted;
import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteLensesHandler {

  private final WorkOrderRepository workOrderRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public CompleteLensesHandler(
      WorkOrderRepository workOrderRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(CompleteLensesCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Instant now = clock.instant();
    List<LensId> completed = workOrder.completeLenses(command.lensIds(), now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new LensesWereCompleted(workOrder.id(), completed, now));
    return workOrder;
  }
}
