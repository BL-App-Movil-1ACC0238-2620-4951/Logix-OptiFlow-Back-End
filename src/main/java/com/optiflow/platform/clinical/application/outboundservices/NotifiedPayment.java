package com.optiflow.platform.clinical.application.outboundservices;

import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentMethod;
import com.optiflow.platform.clinical.domain.model.valueobjects.PaymentStatus;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;

/**
 * Payment of the gateway translated to the model of Clinical & Commercial.
 *
 * @param transactionReference id of the payment in the gateway
 * @param statusDetail reason of a rejection given by the gateway; empty when approved
 */
public record NotifiedPayment(
    SaleId saleId,
    PaymentMethod method,
    Money amount,
    String transactionReference,
    PaymentStatus status,
    String statusDetail) {
}
