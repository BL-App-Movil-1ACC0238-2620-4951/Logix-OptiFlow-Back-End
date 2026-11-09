package com.optiflow.platform.inventory.domain.model.queries;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;

public record GetLowStockAlertsQuery(OpticalStoreId opticalStoreId) {
}
