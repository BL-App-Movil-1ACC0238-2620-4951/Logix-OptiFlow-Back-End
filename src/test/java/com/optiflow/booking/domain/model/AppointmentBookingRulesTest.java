package com.optiflow.booking.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.booking.domain.exception.TimeSlotUnavailableException;
import com.optiflow.booking.domain.factory.AppointmentFactory;
import com.optiflow.booking.domain.service.AppointmentAvailabilityService;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
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
  void rejectsATimeSlotThatWasAlreadyReserved() {
    Instant now = Instant.parse("2026-10-06T15:00:00Z");
    TimeSlot timeSlot = availableSlot(now.plus(1, ChronoUnit.DAYS));
    timeSlot.reserve(now);

    assertThrows(TimeSlotUnavailableException.class, () -> timeSlot.reserve(now));
  }

  private TimeSlot availableSlot(Instant start) {
    return TimeSlot.publish(
        TimeSlotId.generate(),
        OpticalStoreId.generate(),
        start,
        start.plus(30, ChronoUnit.MINUTES));
  }
}
