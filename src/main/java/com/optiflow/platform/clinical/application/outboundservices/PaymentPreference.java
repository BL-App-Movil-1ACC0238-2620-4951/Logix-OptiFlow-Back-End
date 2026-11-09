package com.optiflow.platform.clinical.application.outboundservices;

/**
 * Payment preference created in the payment gateway.
 *
 * @param initPoint link that opens the payment page of the gateway in the app
 */
public record PaymentPreference(String id, String initPoint) {
}
