package com.optiflow.platform.notification.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.notification.application.commandhandlers.AnswerSatisfactionSurveyHandler;
import com.optiflow.platform.notification.application.commandhandlers.GrantBirthdayDiscountHandler;
import com.optiflow.platform.notification.application.commandhandlers.MarkNotificationAsReadHandler;
import com.optiflow.platform.notification.application.commandhandlers.NotifyLensOrderProgressHandler;
import com.optiflow.platform.notification.application.commandhandlers.RegisterDeviceTokenHandler;
import com.optiflow.platform.notification.application.commandhandlers.ScheduleAppointmentReminderHandler;
import com.optiflow.platform.notification.application.commandhandlers.SendDeliveryDelayNotificationHandler;
import com.optiflow.platform.notification.application.commandhandlers.SendSatisfactionSurveyHandler;
import com.optiflow.platform.notification.application.queryservices.GetLoyaltyAccountByPatientQueryService;
import com.optiflow.platform.notification.application.queryservices.GetNotificationByIdQueryService;
import com.optiflow.platform.notification.application.queryservices.GetNotificationsByPatientQueryService;
import com.optiflow.platform.notification.domain.exceptions.LoyaltyAccountNotFoundException;
import com.optiflow.platform.notification.domain.model.commands.AnswerSatisfactionSurveyCommand;
import com.optiflow.platform.notification.domain.model.commands.GrantBirthdayDiscountCommand;
import com.optiflow.platform.notification.domain.model.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.domain.model.commands.ScheduleAppointmentReminderCommand;
import com.optiflow.platform.notification.domain.model.commands.SendSatisfactionSurveyCommand;
import com.optiflow.platform.notification.domain.model.queries.GetLoyaltyAccountByPatientQuery;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.support.FakeDeviceTokenRepository;
import com.optiflow.platform.notification.support.FakeLoyaltyAccountRepository;
import com.optiflow.platform.notification.support.FakeNotificationRepository;
import com.optiflow.platform.notification.support.FakePatientDirectory;
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
  private FakePatientDirectory patientDirectory;
  private NotificationApplicationService notificationApplicationService;
  private LoyaltyApplicationService loyaltyApplicationService;

  @BeforeEach
  void setUp() {
    notificationRepository = new FakeNotificationRepository();
    loyaltyAccountRepository = new FakeLoyaltyAccountRepository();
    patientDirectory = new FakePatientDirectory();
    Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

    ScheduleAppointmentReminderHandler scheduleAppointmentReminderHandler =
        new ScheduleAppointmentReminderHandler(
            notificationRepository, patientDirectory, eventPublisher, clock);
    GrantBirthdayDiscountHandler grantBirthdayDiscountHandler =
        new GrantBirthdayDiscountHandler(
            loyaltyAccountRepository, notificationRepository, eventPublisher, clock);
    MarkNotificationAsReadHandler markNotificationAsReadHandler =
        new MarkNotificationAsReadHandler(notificationRepository, clock);
    GetNotificationsByPatientQueryService getNotificationsByPatientQueryService =
        new GetNotificationsByPatientQueryService(notificationRepository, clock);
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
        new AnswerSatisfactionSurveyHandler(notificationRepository, eventPublisher, clock),
        getNotificationsByPatientQueryService,
        getNotificationByIdQueryService);
    loyaltyApplicationService = new LoyaltyApplicationService(getLoyaltyAccountByPatientQueryService);
  }

  @Test
  void schedulesTheReminderADayBeforeAndTheAnnualCheckupAYearLater() {
    patientDirectory.book(APPOINTMENT_ID, PATIENT_ID, Instant.parse("2026-10-10T14:00:00Z"));

    var reminder = notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));

    assertEquals(NotificationType.APPOINTMENT_REMINDER, reminder.type());
    assertEquals(Instant.parse("2026-10-09T14:00:00Z"), reminder.scheduledAt().orElseThrow());
    assertEquals("Tu evaluación visual es el 10/10 a las 09:00.", reminder.message().body());
    assertEquals(Instant.parse("2027-10-10T14:00:00Z"), notificationRepository.findAll().stream()
        .filter(notification -> notification.type() == NotificationType.ANNUAL_CHECKUP_REMINDER)
        .findFirst().orElseThrow().scheduledAt().orElseThrow());
    assertEquals(0, notificationApplicationService.findByPatient(
        new GetNotificationsByPatientQuery(PATIENT_ID)).size());
  }

  @Test
  void remindsRightAwayAnAppointmentInLessThanADayAndMarksItAsRead() {
    patientDirectory.book(APPOINTMENT_ID, PATIENT_ID, NOW.plusSeconds(3 * 3600));

    var notification = notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));

    assertEquals(NOW, notification.scheduledAt().orElseThrow());
    assertEquals(1, notificationApplicationService.findByPatient(
        new GetNotificationsByPatientQuery(PATIENT_ID)).size());

    var read = notificationApplicationService.markAsRead(
        new MarkNotificationAsReadCommand(notification.id().value()));
    assertEquals(NotificationStatus.READ, read.status());
  }

  @Test
  void doesNotDuplicateAppointmentReminders() {
    patientDirectory.book(APPOINTMENT_ID, PATIENT_ID, NOW.plusSeconds(3600));
    notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));
    notificationApplicationService.scheduleAppointmentReminder(
        new ScheduleAppointmentReminderCommand(PATIENT_ID, APPOINTMENT_ID));

    assertEquals(2, notificationRepository.findAll().size());
  }

  @Test
  void sendsASurveyForADeliveredOrderAndSavesItsAnswerOnce() {
    var survey = new SendSatisfactionSurveyHandler(notificationRepository,
        Mockito.mock(DomainEventPublisher.class), Clock.fixed(NOW, ZoneOffset.UTC))
        .handle(new SendSatisfactionSurveyCommand(PATIENT_ID, UUID.randomUUID()));

    var answered = notificationApplicationService.answerSurvey(
        new AnswerSatisfactionSurveyCommand(survey.id().value(), 4, "  Muy buena atención. "));

    assertEquals(4, answered.surveyResponse().orElseThrow().rating());
    assertEquals("Muy buena atención.", answered.surveyResponse().orElseThrow().comment());
    assertEquals(NotificationStatus.READ, answered.status());
    DomainException exception = assertThrows(DomainException.class,
        () -> notificationApplicationService.answerSurvey(
            new AnswerSatisfactionSurveyCommand(survey.id().value(), 5, null)));
    assertEquals(409, exception.status());
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
