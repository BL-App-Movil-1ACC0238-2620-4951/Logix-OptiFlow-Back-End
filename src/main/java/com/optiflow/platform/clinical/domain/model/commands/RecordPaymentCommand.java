package com.optiflow.platform.clinical.domain.model.commands;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

/**
 * Records the payment of a sale: approved or rejected by the payment gateway, or made in cash.
 *
 * @param statusDetail reason of a rejected payment; empty when approved
 */
public record RecordPaymentCommand(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    String transactionReference,
    PaymentStatus status,
    String statusDetail) {
}
