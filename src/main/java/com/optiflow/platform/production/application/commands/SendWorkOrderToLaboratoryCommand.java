package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;

public record SendWorkOrderToLaboratoryCommand(WorkOrderId workOrderId, LaboratoryId laboratoryId) {
}
