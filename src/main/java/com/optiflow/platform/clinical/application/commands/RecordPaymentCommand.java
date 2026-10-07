package com.optiflow.platform.clinical.application.commands;

import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;

public record RecordPaymentCommand(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    String transactionReference) {
}
