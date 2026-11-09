package com.optiflow.platform.notification.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the scheduled tasks of Notification &amp; Loyalty: the notification dispatcher and the
 * daily birthday discount, which runs in the time zone of Lima.
 */
@Configuration
@EnableScheduling
public class NotificationSchedulingConfiguration {
}
