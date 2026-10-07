package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.application.commands.UpdateWorkOrderStatusCommand;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.interfaces.rest.resources.UpdateWorkOrderStatusRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromUpdateWorkOrderStatusRequestAssembler {

  public UpdateWorkOrderStatusCommand toCommand(
      UUID workOrderId, UpdateWorkOrderStatusRequest request) {
    return new UpdateWorkOrderStatusCommand(
        WorkOrderId.of(workOrderId), WorkOrderStatus.from(request.status()));
  }
}
