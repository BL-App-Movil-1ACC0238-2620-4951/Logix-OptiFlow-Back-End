package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;

public record UpdateWorkOrderStatusCommand(WorkOrderId workOrderId, WorkOrderStatus status) {
}
