package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;

public record SendWorkOrderToLaboratoryCommand(WorkOrderId workOrderId, LaboratoryId laboratoryId) {
}
