package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.entities.Lenses;
import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.valueobjects.StatusChange;
import com.optiflow.platform.production.interfaces.rest.resources.DeliveryDelayResponse;
import com.optiflow.platform.production.interfaces.rest.resources.LaboratoryResponse;
import com.optiflow.platform.production.interfaces.rest.resources.LensResponse;
import com.optiflow.platform.production.interfaces.rest.resources.StatusChangeResponse;
import com.optiflow.platform.production.interfaces.rest.resources.TechnicianResponse;
import com.optiflow.platform.production.interfaces.rest.resources.WorkOrderResponse;
import com.optiflow.platform.production.interfaces.rest.resources.WorkOrderStatusResponse;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ProductionTrackingResponseAssembler {

  private final Clock clock;

  public ProductionTrackingResponseAssembler(Clock clock) {
    this.clock = clock;
  }

  public WorkOrderResponse toWorkOrderResponse(WorkOrder workOrder) {
    return new WorkOrderResponse(
        workOrder.id().value(),
        workOrder.saleId().value(),
        workOrder.patientId().value(),
        workOrder.opticalStoreId().map(store -> store.value()).orElse(null),
        workOrder.status().name(),
        workOrder.estimatedDeliveryDate().date(),
        workOrder.isDelayed(LocalDate.now(clock)),
        workOrder.technician().map(this::toTechnicianResponse).orElse(null),
        workOrder.laboratory().map(this::toLaboratoryResponse).orElse(null),
        workOrder.lenses().stream().map(this::toLensResponse).toList(),
        workOrder.deliveryDelay().map(this::toDelayResponse).orElse(null),
        workOrder.statusHistory().stream().map(this::toStatusChangeResponse).toList(),
        workOrder.createdAt(),
        workOrder.updatedAt(),
        workOrder.deliveredAt());
  }

  public WorkOrderStatusResponse toStatusResponse(WorkOrder workOrder) {
    return new WorkOrderStatusResponse(
        workOrder.id().value(), workOrder.status().name(), workOrder.updatedAt());
  }

  public TechnicianResponse toTechnicianResponse(Technician technician) {
    return new TechnicianResponse(technician.id().value(), technician.name());
  }

  public LaboratoryResponse toLaboratoryResponse(Laboratory laboratory) {
    return new LaboratoryResponse(laboratory.id().value(), laboratory.name());
  }

  private LensResponse toLensResponse(Lenses lens) {
    return new LensResponse(lens.id().value(), lens.specifications(), lens.completed());
  }

  private DeliveryDelayResponse toDelayResponse(DeliveryDelay delay) {
    return new DeliveryDelayResponse(
        delay.reason(), delay.reportedAt(), delay.newEstimatedDeliveryDate());
  }

  private StatusChangeResponse toStatusChangeResponse(StatusChange change) {
    return new StatusChangeResponse(change.status().name(), change.changedAt());
  }
}
