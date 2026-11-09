package com.optiflow.platform.inventory.domain.model.queries;

import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import java.time.LocalDate;

public record GetInventoryMetricsQuery(OpticalStoreId opticalStoreId, LocalDate from, LocalDate to) {
}
