package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.application.commands.CompleteLensesCommand;
import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.interfaces.rest.resources.CompleteLensesRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromCompleteLensesRequestAssembler {

  public CompleteLensesCommand toCommand(UUID workOrderId, CompleteLensesRequest request) {
    List<LensId> lensIds = request == null || request.lensIds() == null
        ? List.of()
        : request.lensIds().stream().map(LensId::of).toList();
    return new CompleteLensesCommand(WorkOrderId.of(workOrderId), lensIds);
  }
}
