package com.optiflow.platform.searchbooking.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.searchbooking.domain.exceptions.TimeSlotUnavailableException;
import com.optiflow.platform.searchbooking.domain.services.AppointmentAvailabilityService;
import com.optiflow.platform.searchbooking.domain.services.AppointmentFactory;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentStatus;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotStatus;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class AppointmentBookingRulesTest {

  private final AppointmentAvailabilityService availabilityService = new AppointmentAvailabilityService();
  private final AppointmentFactory appointmentFactory = new AppointmentFactory();

  @Test
  void booksAndConfirmsWhenTheTimeSlotIsAvailable() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    PatientId patientId = PatientId.generate();
    OpticalStoreId storeId = timeSlot.opticalStoreId();

    availabilityService.ensureAvailable(timeSlot, now);
    timeSlot.reserve(now);
    Appointment appointment = appointmentFactory.book(patientId, storeId, timeSlot, now);
    appointment.confirm(now);

    assertEquals(AppointmentStatus.CONFIRMED, appointment.status());
    assertEquals(TimeSlotStatus.RESERVED, timeSlot.status());
  }

  @Test
  void cancelsAConfirmedAppointmentAndReleasesTheTimeSlot() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    timeSlot.reserve(now);
    Appointment appointment = confirmedAppointment(timeSlot, now);

    appointment.cancel(now);
    timeSlot.release();

    assertEquals(AppointmentStatus.CANCELLED, appointment.status());
    assertEquals(TimeSlotStatus.AVAILABLE, timeSlot.status());
  }

  @Test
  void completesOnlyAConfirmedAppointment() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    Appointment appointment = confirmedAppointment(timeSlot, now);

    appointment.complete(now);

    assertEquals(AppointmentStatus.COMPLETED, appointment.status());
  }

  @Test
  void rejectsCompletingAPendingAppointment() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    Appointment appointment = appointmentFactory.book(
        PatientId.generate(), timeSlot.opticalStoreId(), timeSlot, now);

    assertThrows(DomainException.class, () -> appointment.complete(now));
  }

  @Test
  void rejectsATimeSlotThatWasAlreadyReserved() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    timeSlot.reserve(now);

    assertThrows(TimeSlotUnavailableException.class, () -> timeSlot.reserve(now));
  }

  private Appointment confirmedAppointment(TimeSlot timeSlot, Instant now) {
    timeSlot.reserve(now);
    Appointment appointment = appointmentFactory.book(
        PatientId.generate(), timeSlot.opticalStoreId(), timeSlot, now);
    appointment.confirm(now);
    return appointment;
  }

  private TimeSlot availableSlot(Instant start) {
    return TimeSlot.publish(
        TimeSlotId.generate(),
        OpticalStoreId.generate(),
        start,
        start.plus(30, ChronoUnit.MINUTES));
  }
}
