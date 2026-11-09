package com.optiflow.platform.notification.infrastructure.outboundservices;

import java.util.Map;

/** Notification in the format of Firebase Cloud Messaging: visible text and app data. */
public record FcmMessage(String title, String body, Map<String, String> data) {
}
