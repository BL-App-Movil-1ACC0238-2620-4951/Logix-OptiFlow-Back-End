package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.commands.AddNewFrameModelCommand;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.resources.AddNewFrameModelRequest;
import org.springframework.stereotype.Component;

@Component
public class FromAddNewFrameModelRequestAssembler {

  public AddNewFrameModelCommand toCommand(AddNewFrameModelRequest request) {
    return new AddNewFrameModelCommand(
        Sku.of(request.sku()),
        request.name(),
        request.material(),
        request.shape(),
        request.color(),
        Price.of(request.price()),
        request.tryOnModelUrl());
  }
}
