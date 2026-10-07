package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.SaleId;

public record CloseSaleCommand(SaleId saleId) {
}
