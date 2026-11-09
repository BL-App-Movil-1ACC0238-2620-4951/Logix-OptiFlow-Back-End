package com.optiflow.platform.clinical.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mercado Pago Checkout Pro settings. The access token and the webhook secret come from the
 * {@code MERCADOPAGO_ACCESS_TOKEN} and {@code MERCADOPAGO_WEBHOOK_SECRET} environment variables.
 *
 * @param backUrl deep link that takes the patient back to the app after paying
 */
@ConfigurationProperties(prefix = "optiflow.payments.mercadopago")
public record MercadoPagoProperties(
    String accessToken,
    String webhookSecret,
    String baseUrl,
    String notificationUrl,
    String backUrl) {
}
