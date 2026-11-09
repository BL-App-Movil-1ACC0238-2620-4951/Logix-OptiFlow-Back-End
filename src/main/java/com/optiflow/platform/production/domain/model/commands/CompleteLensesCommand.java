package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.LensId;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import java.util.List;

public record CompleteLensesCommand(WorkOrderId workOrderId, List<LensId> lensIds) {
}
