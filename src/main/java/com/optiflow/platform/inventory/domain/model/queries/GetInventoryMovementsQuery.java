package com.optiflow.platform.inventory.domain.model.queries;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import java.time.LocalDate;

/** @param sku optional filter by frame model */
public record GetInventoryMovementsQuery(OpticalStoreId opticalStoreId, LocalDate from, LocalDate to, Sku sku) {
}
