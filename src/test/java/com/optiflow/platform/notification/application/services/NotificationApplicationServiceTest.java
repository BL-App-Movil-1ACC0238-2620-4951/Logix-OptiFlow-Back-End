package com.optiflow.platform.notification.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.notification.application.commandhandlers.GrantBirthdayDiscountHandler;
import com.optiflow.platform.notification.application.commandhandlers.MarkNotificationAsReadHandler;
import com.optiflow.platform.notification.application.commandhandlers.NotifyLensOrderProgressHandler;
import com.optiflow.platform.notification.application.commandhandlers.RegisterDeviceTokenHandler;
import com.optiflow.platform.notification.application.commandhandlers.ScheduleAppointmentReminderHandler;
import com.optiflow.platform.notification.application.commandhandlers.SendDeliveryDelayNotificationHandler;
import com.optiflow.platform.notification.application.queryservices.GetLoyaltyAccountByPatientQueryService;
import com.optiflow.platform.notification.application.queryservices.GetNotificationByIdQueryService;
import com.optiflow.platform.notification.application.queryservices.GetNotificationsByPatientQueryService;
import com.optiflow.platform.notification.domain.exceptions.LoyaltyAccountNotFoundException;
import com.optiflow.platform.notification.domain.model.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.domain.model.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.domain.model.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.support.FakeDeviceTokenRepository;
import com.optiflow.platform.notification.support.FakeLoyaltyAccountRepository;
import com.optiflow.platform.notification.support.FakeNotificationRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationApplicationServiceTest {

  private static final UUID PATIENT_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  private static final UUID APPOINTMENT_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
  private static final Instant NOW = Instant.parse("2026-10-07T15:00:00Z");

  private FakeNotificationRepository notificationRepository;
  private FakeLoyaltyAccountRepository loyaltyAccountRepository;
  private NotificationApplicationService notificationApplicationService;
  private LoyaltyApplicationService loyaltyApplicationService;

  @BeforeEach
  void setUp() {
    notificationRepository = new FakeNotificationRepository();
    loyaltyAccountRepository = new FakeLoyaltyAccountRepository();
    Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

    ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler =
        new ScheduleAppointmentReminderHandler(notificationRepository, eventPublisher, clock);
    GrantBirthdayDiscountHandler grantBirthdayDiscountHandler =
        new GrantBirthdayDiscountHandler(
            loyaltyAccountRepository, notificationRepository, eventPublisher, clock);
    MarkNotificationAsReadHandler markNotificationAsReadHandler =
        new MarkNotificationAsReadHandler(notificationRepository, clock);
    GetNotificationsByPatientQueryService getNotificationsByPatientQueryService =
        new GetNotificationsByPatientQueryService(notificationRepository);
    GetNotificationByIdQueryService getNotificationByIdQueryService =
        new GetNotificationByIdQueryService(notificationRepository);
    GetLoyaltyAccountByPatientQueryService getLoyaltyAccountByPatientQueryService =
        new GetLoyaltyAccountByPatientQueryService(loyaltyAccountRepository);

    notificationApplicationService = new NotificationApplicationService(
        scheduleAppointmentReminderHandler,
        new NotifyLensOrderProgressHandler(notificationRepository, eventPublisher, clock),
        new SendDeliveryDelayNotificationHandler(notificationRepository, eventPublisher, clock),
        grantBirthdayDiscountHandler,
        markNotificationAsReadHandler,
        new RegisterDeviceTokenHandler(new FakeDeviceTokenRepository(), eventPublisher, clock),
        getNotificationsByPatientQueryService,
        getNotificationByIdQueryService);
    loyaltyApplicationService = new LoyaltyApplicationService(getLoyaltyAccountByPatientQueryService);
  }

  @Test
  void schedulesAppointmentReminderAndMarksItAsRead() {
    var notification = notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));

    assertEquals(NotificationType.APPOINTMENT_REMINDER, notification.type());
    assertEquals(1, notificationApplicationService.findByPatient(
        new GetNotificationsByPatientQuery(PATIENT_ID)).size());

    var read = notificationApplicationService.markAsRead(
        new MarkNotificationAsReadCommand(notification.id().value()));
    assertEquals(NotificationStatus.READ, read.status());
  }

  @Test
  void doesNotDuplicateAppointmentReminders() {
    notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));
    notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));

    assertEquals(1, notificationApplicationService.findByPatient(
        new GetNotificationsByPatientQuery(PATIENT_ID)).size());
  }

  @Test
  void grantsBirthdayDiscountAndOpensLoyaltyAccount() {
    notificationApplicationService.grantBirthdayDiscount(
        new GrantBirthdayDiscountCommand(PATIENT_ID, 2026));

    assertEquals(100, loyaltyApplicationService.getByPatient(
        new GetLoyaltyAccountByPatientQuery(PATIENT_ID)).balance().value());

    DomainException exception = assertThrows(DomainException.class,
        () -> notificationApplicationService.grantBirthdayDiscount(
            new GrantBirthdayDiscountCommand(PATIENT_ID, 2026)));
    assertEquals(409, exception.status());
  }

  @Test
  void loyaltyAccountMustExistBeforeItIsQueried() {
    assertThrows(LoyaltyAccountNotFoundException.class,
        () -> loyaltyApplicationService.getByPatient(
            new GetLoyaltyAccountByPatientQuery(PATIENT_ID)));
  }
}
