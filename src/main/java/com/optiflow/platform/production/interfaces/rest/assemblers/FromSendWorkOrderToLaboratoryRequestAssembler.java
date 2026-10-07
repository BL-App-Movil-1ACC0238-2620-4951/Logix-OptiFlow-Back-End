package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.application.commands.SendWorkOrderToLaboratoryCommand;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.domain.valueobjects.WorkOrderId;
import com.optiflow.platform.production.interfaces.rest.resources.SendWorkOrderToLaboratoryRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromSendWorkOrderToLaboratoryRequestAssembler {

  public SendWorkOrderToLaboratoryCommand toCommand(
      UUID workOrderId, SendWorkOrderToLaboratoryRequest request) {
    return new SendWorkOrderToLaboratoryCommand(
        WorkOrderId.of(workOrderId), LaboratoryId.of(request.laboratoryId()));
  }
}
