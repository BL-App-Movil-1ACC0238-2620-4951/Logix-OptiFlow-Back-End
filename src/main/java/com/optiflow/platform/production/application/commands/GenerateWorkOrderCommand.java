package com.optiflow.platform.production.application.commands;

import com.optiflow.platform.production.domain.valueobjects.SaleId;

public record GenerateWorkOrderCommand(SaleId saleId) {
}
