package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.domain.model.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.domain.model.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.domain.model.commands.NotifyLensOrderProgressCommand;
import com.optiflow.platform.notification.domain.model.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.commands.SendDeliveryDelayNotificationCommand;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationByIdQuery;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import java.util.List;
import org.springframework.stereotype.Service;
import com.optiflow.platform.notification.application.commandhandlers.GrantBirthdayDiscountHandler;
import com.optiflow.platform.notification.application.commandhandlers.MarkNotificationAsReadHandler;
import com.optiflow.platform.notification.application.commandhandlers.NotifyLensOrderProgressHandler;
import com.optiflow.platform.notification.application.commandhandlers.ScheduleAppointmentReminderHandler;
import com.optiflow.platform.notification.application.commandhandlers.SendDeliveryDelayNotificationHandler;
import com.optiflow.platform.notification.application.queryservices.GetNotificationByIdQueryService;
import com.optiflow.platform.notification.application.queryservices.GetNotificationsByPatientQueryService;

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
