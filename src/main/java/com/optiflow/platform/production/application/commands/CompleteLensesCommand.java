package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import java.util.List;

public record CompleteLensesCommand(WorkOrderId workOrderId, List<LensId> lensIds) {
}
