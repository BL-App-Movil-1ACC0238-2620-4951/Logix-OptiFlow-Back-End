package com.optiflow.platform.production.application.services;

import com.optiflow.platform.production.application.queries.GetWorkOrderByIdQuery;
import com.optiflow.platform.production.application.queries.GetWorkOrdersByPatientIdQuery;
import com.optiflow.platform.production.application.queries.GetWorkOrdersQuery;
import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.exceptions.WorkOrderNotFoundException;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderQueryService {

  private final WorkOrderRepository workOrderRepository;

  public WorkOrderQueryService(WorkOrderRepository workOrderRepository) {
    this.workOrderRepository = workOrderRepository;
  }

  @Transactional(readOnly = true)
  public WorkOrder handle(GetWorkOrderByIdQuery query) {
    return workOrderRepository.findById(query.workOrderId())
        .orElseThrow(WorkOrderNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public List<WorkOrder> handle(GetWorkOrdersQuery query) {
    return workOrderRepository.search(
        query.status(), query.technicianId(), query.opticalStoreId());
  }

  @Transactional(readOnly = true)
  public List<WorkOrder> handle(GetWorkOrdersByPatientIdQuery query) {
    return workOrderRepository.findByPatientId(query.patientId());
  }
}
