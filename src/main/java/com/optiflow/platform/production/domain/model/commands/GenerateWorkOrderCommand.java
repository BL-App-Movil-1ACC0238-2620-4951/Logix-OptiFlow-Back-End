package com.optiflow.platform.production.domain.model.commands;

import com.optiflow.platform.production.domain.model.valueobjects.SaleId;

public record GenerateWorkOrderCommand(SaleId saleId) {
}
