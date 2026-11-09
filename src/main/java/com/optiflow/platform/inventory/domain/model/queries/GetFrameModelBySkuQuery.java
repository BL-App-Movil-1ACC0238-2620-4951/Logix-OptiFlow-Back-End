package com.optiflow.platform.inventory.domain.model.queries;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;

/** @param opticalStoreId optional; when present the result includes that store's stock */
public record GetFrameModelBySkuQuery(Sku sku, OpticalStoreId opticalStoreId) {
}
