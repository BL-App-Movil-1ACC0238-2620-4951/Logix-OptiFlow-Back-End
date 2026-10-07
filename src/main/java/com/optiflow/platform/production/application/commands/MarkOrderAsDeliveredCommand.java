package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;

public record MarkOrderAsDeliveredCommand(WorkOrderId workOrderId) {
}
