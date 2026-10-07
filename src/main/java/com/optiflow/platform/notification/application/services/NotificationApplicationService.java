package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.application.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.application.commands.NotifyLensOrderProgressCommand;
import com.optiflow.platform.notification.application.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.application.commands.SendDeliveryDelayNotificationCommand;
import com.optiflow.platform.notification.application.queries.GetNotificationByIdQuery;
import com.optiflow.platform.notification.application.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.entities.Notification;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationApplicationService {

  private final ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler;
  private final NotifyLensOrderProgressHandler notifyLensOrderProgressHandler;
  private final SendDeliveryDelayNotificationHandler sendDeliveryDelayNotificationHandler;
  private final GrantBirthdayDiscountHandler grantBirthdayDiscountHandler;
  private final MarkNotificationAsReadHandler markNotificationAsReadHandler;
  private final GetNotificationsByPatientQueryService getNotificationsByPatientQueryService;
  private final GetNotificationByIdQueryService getNotificationByIdQueryService;

  public NotificationApplicationService(
      ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler,
      NotifyLensOrderProgressHandler notifyLensOrderProgressHandler,
      SendDeliveryDelayNotificationHandler sendDeliveryDelayNotificationHandler,
      GrantBirthdayDiscountHandler grantBirthdayDiscountHandler,
      MarkNotificationAsReadHandler markNotificationAsReadHandler,
      GetNotificationsByPatientQueryService getNotificationsByPatientQueryService,
      GetNotificationByIdQueryService getNotificationByIdQueryService) {
    this.scheduleAppointmentReminderHandler = scheduleAppointmentReminderHandler;
    this.notifyLensOrderProgressHandler = notifyLensOrderProgressHandler;
    this.sendDeliveryDelayNotificationHandler = sendDeliveryDelayNotificationHandler;
    this.grantBirthdayDiscountHandler = grantBirthdayDiscountHandler;
    this.markNotificationAsReadHandler = markNotificationAsReadHandler;
    this.getNotificationsByPatientQueryService = getNotificationsByPatientQueryService;
    this.getNotificationByIdQueryService = getNotificationByIdQueryService;
  }

  public Notification scheduleAppointmentReminder(ScheduleAppointmentReminderCommand command) {
    return scheduleAppointmentReminderHandler.handle(command);
  }

  public Notification notifyLensOrderProgress(NotifyLensOrderProgressCommand command) {
    return notifyLensOrderProgressHandler.handle(command);
  }

  public Notification sendDeliveryDelayNotification(SendDeliveryDelayNotificationCommand command) {
    return sendDeliveryDelayNotificationHandler.handle(command);
  }

  public Notification grantBirthdayDiscount(GrantBirthdayDiscountCommand command) {
    return grantBirthdayDiscountHandler.handle(command);
  }

  public Notification markAsRead(MarkNotificationAsReadCommand command) {
    return markNotificationAsReadHandler.handle(command);
  }

  public List<Notification> findByPatient(GetNotificationsByPatientQuery query) {
    return getNotificationsByPatientQueryService.handle(query);
  }

  public Notification getById(GetNotificationByIdQuery query) {
    return getNotificationByIdQueryService.handle(query);
  }
}
