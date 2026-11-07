package com.optiflow.platform.production.domain.model.queries;

import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;

public record GetWorkOrderByIdQuery(WorkOrderId workOrderId) {
}
