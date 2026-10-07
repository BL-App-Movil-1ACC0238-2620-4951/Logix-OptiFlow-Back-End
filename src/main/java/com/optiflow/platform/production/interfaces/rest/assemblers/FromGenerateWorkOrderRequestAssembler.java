package com.optiflow.platform.production.interfaces.rest.assemblers;

import com.optiflow.platform.production.application.commands.GenerateWorkOrderCommand;
import com.optiflow.platform.production.domain.valueobjects.SaleId;
import com.optiflow.platform.production.interfaces.rest.resources.GenerateWorkOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class FromGenerateWorkOrderRequestAssembler {

  public GenerateWorkOrderCommand toCommand(GenerateWorkOrderRequest request) {
    return new GenerateWorkOrderCommand(SaleId.of(request.saleId()));
  }
}
