package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

public record RecordPaymentCommand(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    String transactionReference) {
}
