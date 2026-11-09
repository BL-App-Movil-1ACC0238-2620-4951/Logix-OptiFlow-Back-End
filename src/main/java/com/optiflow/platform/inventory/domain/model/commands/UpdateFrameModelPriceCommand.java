package com.optiflow.platform.inventory.domain.model.commands;

import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;

public record UpdateFrameModelPriceCommand(Sku sku, Price price) {
}
