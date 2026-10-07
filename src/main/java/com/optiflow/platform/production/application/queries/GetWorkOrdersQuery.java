package com.optiflow.platform.production.application.queries;

import com.optiflow.platform.production.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;

public record GetWorkOrdersQuery(
    WorkOrderStatus status,
    TechnicianId technicianId,
    OpticalStoreId opticalStoreId) {
}
