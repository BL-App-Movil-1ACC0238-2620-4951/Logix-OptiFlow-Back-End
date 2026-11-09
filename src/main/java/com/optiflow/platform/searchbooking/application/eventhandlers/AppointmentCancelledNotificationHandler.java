package com.optiflow.platform.searchbooking.application.eventhandlers;

import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCancelled;
import com.optiflow.platform.searchbooking.infrastructure.outboundservices.NotificationIntegrationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentCancelledNotificationHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentCancelledNotificationHandler.class);
  private final NotificationIntegrationAdapter notificationIntegrationAdapter;

  public AppointmentCancelledNotificationHandler(
      NotificationIntegrationAdapter notificationIntegrationAdapter) {
    this.notificationIntegrationAdapter = notificationIntegrationAdapter;
  }

  @EventListener
  public void onAppointmentCancelled(AppointmentCancelled event) {
    notificationIntegrationAdapter.dismissAppointmentReminder(event);
    LOGGER.info(
        "Appointment {} cancellation published for Notification & Loyalty.",
        event.appointmentId().value());
  }
}
