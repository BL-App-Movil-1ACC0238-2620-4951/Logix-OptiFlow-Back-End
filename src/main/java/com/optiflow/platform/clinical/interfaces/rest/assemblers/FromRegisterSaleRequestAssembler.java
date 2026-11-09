package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterSaleRequest;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterSaleRequestAssembler {

  public RegisterSaleCommand toCommand(RegisterSaleRequest request) {
    return new RegisterSaleCommand(QuotationId.of(request.quotationId()));
  }
}
