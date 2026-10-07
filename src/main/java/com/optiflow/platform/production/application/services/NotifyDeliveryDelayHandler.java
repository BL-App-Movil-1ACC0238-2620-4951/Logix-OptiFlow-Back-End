package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.commands.NotifyDeliveryDelayCommand;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.events.DeliveryDelayNotified;
import com.optiflow.platform.production.domain.events.EstimatedDeliveryDateCalculated;
import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotifyDeliveryDelayHandler {

  private final WorkOrderRepository workOrderRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public NotifyDeliveryDelayHandler(
      WorkOrderRepository workOrderRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public WorkOrder handle(NotifyDeliveryDelayCommand command) {
    WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
    Instant now = clock.instant();
    workOrder.notifyDeliveryDelay(
        command.reason(), command.newEstimatedDeliveryDate(), LocalDate.now(clock), now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(new DeliveryDelayNotified(
        workOrder.id(), workOrder.patientId(), workOrder.deliveryDelay().orElseThrow(), now));
    eventPublisher.publish(new EstimatedDeliveryDateCalculated(
        workOrder.id(), workOrder.estimatedDeliveryDate(), now));
    return workOrder;
  }
}
