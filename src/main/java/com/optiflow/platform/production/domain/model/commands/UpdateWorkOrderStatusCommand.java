package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;

public record UpdateWorkOrderStatusCommand(WorkOrderId workOrderId, WorkOrderStatus status) {
}
