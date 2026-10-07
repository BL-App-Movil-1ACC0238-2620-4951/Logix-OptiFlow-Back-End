package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.mappers.WorkOrderMapper;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

  private final WorkOrderJpaRepository workOrderJpaRepository;
  private final WorkOrderLensJpaRepository workOrderLensJpaRepository;
  private final WorkOrderStatusChangeJpaRepository workOrderStatusChangeJpaRepository;
  private final WorkOrderMapper workOrderMapper;

  public WorkOrderRepositoryImpl(
      WorkOrderJpaRepository workOrderJpaRepository,
      WorkOrderLensJpaRepository workOrderLensJpaRepository,
      WorkOrderStatusChangeJpaRepository workOrderStatusChangeJpaRepository,
      WorkOrderMapper workOrderMapper) {
    this.workOrderJpaRepository = workOrderJpaRepository;
    this.workOrderLensJpaRepository = workOrderLensJpaRepository;
    this.workOrderStatusChangeJpaRepository = workOrderStatusChangeJpaRepository;
    this.workOrderMapper = workOrderMapper;
  }

  @Override
  public void save(WorkOrder workOrder) {
    workOrderJpaRepository.save(workOrderMapper.toEntity(workOrder));
    workOrderLensJpaRepository.saveAll(workOrderMapper.toLensEntities(workOrder));
    workOrderStatusChangeJpaRepository.saveAll(workOrderMapper.toStatusChangeEntities(workOrder));
  }

  @Override
  public Optional<WorkOrder> findById(WorkOrderId id) {
    return workOrderJpaRepository.findById(id.value()).map(this::toDomain);
  }

  @Override
  public boolean existsBySaleId(SaleId saleId) {
    return workOrderJpaRepository.existsBySaleId(saleId.value());
  }

  @Override
  public List<WorkOrder> findByPatientId(PatientId patientId) {
    return workOrderJpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId.value()).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public List<WorkOrder> search(
      WorkOrderStatus status, TechnicianId technicianId, OpticalStoreId opticalStoreId) {
    Specification<WorkOrderEntity> filters = (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (status != null) {
        predicates.add(builder.equal(root.get("status"), status.name()));
      }
      if (technicianId != null) {
        predicates.add(builder.equal(root.get("technicianId"), technicianId.value()));
      }
      if (opticalStoreId != null) {
        predicates.add(builder.equal(root.get("opticalStoreId"), opticalStoreId.value()));
      }
      return builder.and(predicates.toArray(Predicate[]::new));
    };
    return workOrderJpaRepository.findAll(filters, Sort.by("createdAt")).stream()
        .map(this::toDomain)
        .toList();
  }

  private WorkOrder toDomain(WorkOrderEntity entity) {
    return workOrderMapper.toDomain(
        entity,
        workOrderLensJpaRepository.findByWorkOrderIdOrderByLineNumber(entity.getId()),
        workOrderStatusChangeJpaRepository.findByIdWorkOrderIdOrderByIdSequenceNumber(
            entity.getId()));
  }
}
