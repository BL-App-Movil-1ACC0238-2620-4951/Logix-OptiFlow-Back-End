package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.commands.RegisterSupplierCommand;
import com.optiflow.platform.inventory.interfaces.rest.resources.RegisterSupplierRequest;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterSupplierRequestAssembler {

  public RegisterSupplierCommand toCommand(RegisterSupplierRequest request) {
    return new RegisterSupplierCommand(request.name(), request.contact());
  }
}
