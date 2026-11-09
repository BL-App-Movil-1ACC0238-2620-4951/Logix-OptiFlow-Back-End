package com.optiflow.platform.notification.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Firebase Cloud Messaging settings.
 *
 * @param credentials JSON key of the Firebase service account, from the
 *     {@code FIREBASE_CREDENTIALS} environment variable; without it no push is sent
 */
@ConfigurationProperties(prefix = "optiflow.notifications.firebase")
public record FirebaseProperties(String credentials, String baseUrl) {
}
