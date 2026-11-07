package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.domain.model.commands.NotifyDeliveryDelayCommand;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.production.interfaces.rest.resources.NotifyDeliveryDelayRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromNotifyDeliveryDelayRequestAssembler {

  public NotifyDeliveryDelayCommand toCommand(
      UUID workOrderId, NotifyDeliveryDelayRequest request) {
    return new NotifyDeliveryDelayCommand(
        WorkOrderId.of(workOrderId), request.reason(), request.newEstimatedDeliveryDate());
  }
}
