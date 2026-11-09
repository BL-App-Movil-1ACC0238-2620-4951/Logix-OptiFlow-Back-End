package com.optiflow.platform.notification.infrastructure.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Spring configuration for the Notification & Loyalty bounded context. */
@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class NotificationConfiguration {
}
