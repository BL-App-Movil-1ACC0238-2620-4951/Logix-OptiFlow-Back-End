package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelTryOnModelCommand;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.resources.UpdateFrameModelTryOnModelRequest;
import org.springframework.stereotype.Component;

@Component
public class FromUpdateFrameModelTryOnModelRequestAssembler {

  public UpdateFrameModelTryOnModelCommand toCommand(
      String sku, UpdateFrameModelTryOnModelRequest request) {
    return new UpdateFrameModelTryOnModelCommand(Sku.of(sku), request.tryOnModelUrl());
  }
}
