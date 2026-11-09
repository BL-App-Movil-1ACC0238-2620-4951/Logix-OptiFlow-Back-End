package com.optiflow.platform.inventory.domain.model.commands;

import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;

public record AddNewFrameModelCommand(Sku sku, String name, String material, String shape, String color, Price price, String tryOnModelUrl) {
}
