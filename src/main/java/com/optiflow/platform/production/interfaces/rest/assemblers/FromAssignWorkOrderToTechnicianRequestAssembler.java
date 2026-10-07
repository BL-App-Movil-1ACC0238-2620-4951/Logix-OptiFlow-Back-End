package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.application.commands.AssignWorkOrderToTechnicianCommand;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.interfaces.rest.resources.AssignWorkOrderToTechnicianRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromAssignWorkOrderToTechnicianRequestAssembler {

  public AssignWorkOrderToTechnicianCommand toCommand(
      UUID workOrderId, AssignWorkOrderToTechnicianRequest request) {
    return new AssignWorkOrderToTechnicianCommand(
        WorkOrderId.of(workOrderId), TechnicianId.of(request.technicianId()));
  }
}
