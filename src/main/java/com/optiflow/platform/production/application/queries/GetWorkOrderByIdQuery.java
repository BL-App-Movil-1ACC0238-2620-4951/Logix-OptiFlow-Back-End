package com.optiflow.platform.production.application.queries;

import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;

public record GetWorkOrderByIdQuery(WorkOrderId workOrderId) {
}
