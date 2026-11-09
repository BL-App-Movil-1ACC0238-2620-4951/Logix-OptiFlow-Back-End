package com.optiflow.platform.production.domain.model.queries;

import com.optiflow.platform.production.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;

public record GetWorkOrdersQuery(
    WorkOrderStatus status,
    TechnicianId technicianId,
    OpticalStoreId opticalStoreId) {
}
