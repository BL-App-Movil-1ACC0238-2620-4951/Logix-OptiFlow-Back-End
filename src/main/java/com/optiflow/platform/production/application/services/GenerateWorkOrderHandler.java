package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.events.EstimatedDeliveryDateCalculated;
import com.optiflow.platform.production.domain.events.WorkOrderGenerated;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Generates the work order of a closed sale and calculates its estimated delivery date. */
@Service
public class GenerateWorkOrderHandler {

  private final WorkOrderRepository workOrderRepository;
  private final ExternalSaleService externalSaleService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public GenerateWorkOrderHandler(
      WorkOrderRepository workOrderRepository,
      ExternalSaleService externalSaleService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.externalSaleService = externalSaleService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public WorkOrder handle(GenerateWorkOrderCommand command) {
    if (workOrderRepository.existsBySaleId(command.saleId())) {
      throw new DomainException("A work order already exists for this sale.", 409);
    }
    ClosedSale sale = externalSaleService.findSale(command.saleId())
        .orElseThrow(() -> new DomainException("Sale was not found.", 404));
    if (!sale.closed()) {
      throw new DomainException("Only a closed sale can generate a work order.", 409);
    }
    Instant now = clock.instant();
    WorkOrder workOrder = WorkOrder.generate(
        sale.saleId(),
        sale.patientId(),
        sale.opticalStoreId(),
        sale.lensSpecifications(),
        LocalDate.now(clock),
        now);
    workOrderRepository.save(workOrder);
    eventPublisher.publish(
        new WorkOrderGenerated(workOrder.id(), workOrder.saleId(), workOrder.patientId(), now));
    eventPublisher.publish(new EstimatedDeliveryDateCalculated(
        workOrder.id(), workOrder.estimatedDeliveryDate(), now));
    return workOrder;
  }
}
