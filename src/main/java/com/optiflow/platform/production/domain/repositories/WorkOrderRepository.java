package com.optiflow.platform.production.domain.repositories;

import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.model.valueobjects.PatientId;
import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository {

  void save(WorkOrder workOrder);

  Optional<WorkOrder> findById(WorkOrderId id);

  boolean existsBySaleId(SaleId saleId);

  List<WorkOrder> findByPatientId(PatientId patientId);

  /** Kanban search; every filter is optional (null means "any"). */
  List<WorkOrder> search(
      WorkOrderStatus status, TechnicianId technicianId, OpticalStoreId opticalStoreId);
}
