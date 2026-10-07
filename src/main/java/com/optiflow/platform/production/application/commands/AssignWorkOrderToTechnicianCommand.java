package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;

public record AssignWorkOrderToTechnicianCommand(
    WorkOrderId workOrderId,
    TechnicianId technicianId) {
}
