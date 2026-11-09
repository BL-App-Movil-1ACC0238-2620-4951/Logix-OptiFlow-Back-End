package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

public record CloseSaleCommand(SaleId saleId) {
}
