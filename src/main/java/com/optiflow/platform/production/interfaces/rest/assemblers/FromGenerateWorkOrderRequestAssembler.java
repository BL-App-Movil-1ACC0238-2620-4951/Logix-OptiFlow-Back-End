package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.domain.model.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.domain.model.valueobjects.SaleId;
import com.optiflow.platform.production.interfaces.rest.resources.GenerateWorkOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class FromGenerateWorkOrderRequestAssembler {

  public GenerateWorkOrderCommand toCommand(GenerateWorkOrderRequest request) {
    return new GenerateWorkOrderCommand(SaleId.of(request.saleId()));
  }
}
