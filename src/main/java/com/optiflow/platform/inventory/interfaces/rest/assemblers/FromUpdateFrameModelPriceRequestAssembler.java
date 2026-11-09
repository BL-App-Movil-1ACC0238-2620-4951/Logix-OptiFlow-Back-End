package com.optiflow.platform.inventory.interfaces.rest.assemblers;

import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelPriceCommand;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.resources.UpdateFrameModelPriceRequest;
import org.springframework.stereotype.Component;

@Component
public class FromUpdateFrameModelPriceRequestAssembler {

  public UpdateFrameModelPriceCommand toCommand(String sku, UpdateFrameModelPriceRequest request) {
    return new UpdateFrameModelPriceCommand(Sku.of(sku), Price.of(request.price()));
  }
}
