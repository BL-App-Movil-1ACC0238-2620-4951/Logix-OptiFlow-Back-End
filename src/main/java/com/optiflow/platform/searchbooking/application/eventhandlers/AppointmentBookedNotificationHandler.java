package com.optiflow.platform.searchbooking.application.eventhandlers;

import com.optiflow.platform.searchbooking.domain.events.AppointmentBooked;
import com.optiflow.platform.searchbooking.infrastructure.outboundservices.NotificationIntegrationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentBookedNotificationHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AppointmentBookedNotificationHandler.class);
  private final NotificationIntegrationAdapter notificationIntegrationAdapter;

  public AppointmentBookedNotificationHandler(
      NotificationIntegrationAdapter notificationIntegrationAdapter) {
    this.notificationIntegrationAdapter = notificationIntegrationAdapter;
  }

  @EventListener
  public void onAppointmentBooked(AppointmentBooked event) {
    notificationIntegrationAdapter.requestAppointmentReminder(event);
    LOGGER.info(
        "Appointment {} published for Notification & Loyalty.",
        event.appointmentId().value());
  }
}
