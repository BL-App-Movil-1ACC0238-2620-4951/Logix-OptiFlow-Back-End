package com.optiflow.platform.notification.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the scheduled tasks of Notification &amp; Loyalty. Their dates are in the time zone of
 * Lima ({@link #ZONE}).
 */
@Configuration
@EnableScheduling
public class NotificationSchedulingConfiguration {

  public static final String ZONE = "America/Lima";
}
