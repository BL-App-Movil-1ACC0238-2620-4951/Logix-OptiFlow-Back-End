package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;

public record AssignWorkOrderToTechnicianCommand(
    WorkOrderId workOrderId,
    TechnicianId technicianId) {
}
