package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;

public record MarkOrderAsDeliveredCommand(WorkOrderId workOrderId) {
}
