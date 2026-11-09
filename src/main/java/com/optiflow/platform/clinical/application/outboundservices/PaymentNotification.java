package com.optiflow.platform.clinical.application.outboundservices;

/**
 * Notification sent by the payment gateway to the webhook, as received.
 *
 * @param resourceId id of the notified resource (the gateway payment id)
 * @param type notified topic; only {@code payment} is processed
 * @param signature {@code x-signature} header used to check that the gateway sent it
 * @param requestId {@code x-request-id} header, part of the signed content
 */
public record PaymentNotification(
    String resourceId, String type, String signature, String requestId) {
}
