package com.optiflow.platform.production.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.entities.Lenses;
import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.valueobjects.DeliveryDate;
import com.optiflow.platform.production.domain.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.StatusChange;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderLensEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderStatusChangeEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderStatusChangeKey;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/**
 * Maps the work order aggregate. The technician, laboratory and delay are stored as embedded
 * columns of {@code work_orders}; lenses and status history live in their own tables.
 */
@Component
public class WorkOrderMapper {

  public WorkOrder toDomain(
      WorkOrderEntity entity,
      List<WorkOrderLensEntity> lensEntities,
      List<WorkOrderStatusChangeEntity> historyEntities) {
    Technician technician = entity.getTechnicianId() == null
        ? null
        : new Technician(TechnicianId.of(entity.getTechnicianId()), entity.getTechnicianName());
    Laboratory laboratory = entity.getLaboratoryId() == null
        ? null
        : new Laboratory(LaboratoryId.of(entity.getLaboratoryId()), entity.getLaboratoryName());
    DeliveryDelay delay = entity.getDelayReason() == null
        ? null
        : new DeliveryDelay(
            entity.getDelayReason(), entity.getDelayReportedAt(), entity.getDelayNewEstimatedDate());
    return WorkOrder.reconstitute(
        WorkOrderId.of(entity.getId()),
        SaleId.of(entity.getSaleId()),
        PatientId.of(entity.getPatientId()),
        entity.getOpticalStoreId() == null ? null : OpticalStoreId.of(entity.getOpticalStoreId()),
        technician,
        laboratory,
        lensEntities.stream()
            .map(lens -> Lenses.reconstitute(
                LensId.of(lens.getId()), lens.getSpecifications(), lens.getCompleted()))
            .toList(),
        WorkOrderStatus.valueOf(entity.getStatus()),
        new DeliveryDate(entity.getEstimatedDeliveryDate()),
        delay,
        historyEntities.stream()
            .map(change -> new StatusChange(
                WorkOrderStatus.valueOf(change.getStatus()), change.getChangedAt()))
            .toList(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getDeliveredAt());
  }

  public WorkOrderEntity toEntity(WorkOrder workOrder) {
    WorkOrderEntity entity = new WorkOrderEntity();
    entity.setId(workOrder.id().value());
    entity.setSaleId(workOrder.saleId().value());
    entity.setPatientId(workOrder.patientId().value());
    workOrder.opticalStoreId().ifPresent(store -> entity.setOpticalStoreId(store.value()));
    workOrder.technician().ifPresent(technician -> {
      entity.setTechnicianId(technician.id().value());
      entity.setTechnicianName(technician.name());
    });
    workOrder.laboratory().ifPresent(laboratory -> {
      entity.setLaboratoryId(laboratory.id().value());
      entity.setLaboratoryName(laboratory.name());
    });
    entity.setStatus(workOrder.status().name());
    entity.setEstimatedDeliveryDate(workOrder.estimatedDeliveryDate().date());
    workOrder.deliveryDelay().ifPresent(delay -> {
      entity.setDelayReason(delay.reason());
      entity.setDelayReportedAt(delay.reportedAt());
      entity.setDelayNewEstimatedDate(delay.newEstimatedDeliveryDate());
    });
    entity.setCreatedAt(workOrder.createdAt());
    entity.setUpdatedAt(workOrder.updatedAt());
    entity.setDeliveredAt(workOrder.deliveredAt());
    return entity;
  }

  public List<WorkOrderLensEntity> toLensEntities(WorkOrder workOrder) {
    List<Lenses> lenses = workOrder.lenses();
    return IntStream.range(0, lenses.size())
        .mapToObj(index -> {
          WorkOrderLensEntity entity = new WorkOrderLensEntity();
          entity.setId(lenses.get(index).id().value());
          entity.setWorkOrderId(workOrder.id().value());
          entity.setLineNumber(index + 1);
          entity.setSpecifications(lenses.get(index).specifications());
          entity.setCompleted(lenses.get(index).completed());
          return entity;
        })
        .toList();
  }

  public List<WorkOrderStatusChangeEntity> toStatusChangeEntities(WorkOrder workOrder) {
    List<StatusChange> history = workOrder.statusHistory();
    return IntStream.range(0, history.size())
        .mapToObj(index -> {
          WorkOrderStatusChangeEntity entity = new WorkOrderStatusChangeEntity();
          entity.setId(new WorkOrderStatusChangeKey(workOrder.id().value(), index + 1));
          entity.setStatus(history.get(index).status().name());
          entity.setChangedAt(history.get(index).changedAt());
          return entity;
        })
        .toList();
  }
}
