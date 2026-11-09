package com.optiflow.platform.notification.application.eventhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.optiflow.platform.notification.application.commandhandlers.GrantBirthdayDiscountHandler;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.support.FakeLoyaltyAccountRepository;
import com.optiflow.platform.notification.support.FakeNotificationRepository;
import com.optiflow.platform.notification.support.FakePatientDirectory;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BirthdayDiscountSchedulerTest {

  /** 08:00 of November 8 in Lima. */
  private static final Instant NOW = Instant.parse("2026-11-08T13:00:00Z");

  private final FakeNotificationRepository notificationRepository =
      new FakeNotificationRepository();
  private final FakeLoyaltyAccountRepository loyaltyAccountRepository =
      new FakeLoyaltyAccountRepository();
  private final FakePatientDirectory patientDirectory = new FakePatientDirectory();

  @Test
  void grantsTheDiscountOncePerYearToThePatientsBornToday() {
    UUID birthdayPatient = UUID.randomUUID();
    UUID otherPatient = UUID.randomUUID();
    patientDirectory.bornOn(birthdayPatient, LocalDate.of(1995, 11, 8));
    patientDirectory.bornOn(otherPatient, LocalDate.of(1995, 11, 9));
    BirthdayDiscountScheduler scheduler = scheduler();

    scheduler.grantBirthdayDiscounts();
    scheduler.grantBirthdayDiscounts();

    assertEquals(100, loyaltyAccountRepository.findByPatientId(PatientId.of(birthdayPatient))
        .orElseThrow().balance().value());
    assertTrue(loyaltyAccountRepository.findByPatientId(PatientId.of(otherPatient)).isEmpty());
    assertEquals(1, notificationRepository.findAll().size());
    assertEquals(NotificationType.BIRTHDAY_DISCOUNT,
        notificationRepository.findAll().get(0).type());
  }

  private BirthdayDiscountScheduler scheduler() {
    Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    return new BirthdayDiscountScheduler(patientDirectory, new GrantBirthdayDiscountHandler(
        loyaltyAccountRepository, notificationRepository,
        Mockito.mock(DomainEventPublisher.class), clock), clock);
  }
}
