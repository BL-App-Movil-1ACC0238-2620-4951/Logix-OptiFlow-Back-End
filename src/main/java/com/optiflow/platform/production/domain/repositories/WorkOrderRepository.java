package com.optiflow.platform.production.domain.repositories;

import com.optiflow.platform.production.domain.entities.WorkOrder;
import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.PatientId;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
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
