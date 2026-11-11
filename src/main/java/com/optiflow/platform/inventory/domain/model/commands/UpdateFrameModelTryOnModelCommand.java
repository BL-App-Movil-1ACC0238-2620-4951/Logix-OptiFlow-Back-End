package com.optiflow.platform.inventory.domain.model.commands;

import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;

public record UpdateFrameModelTryOnModelCommand(Sku sku, String tryOnModelUrl) {
}
