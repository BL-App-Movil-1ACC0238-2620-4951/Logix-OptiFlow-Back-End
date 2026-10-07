package com.optiflow.booking.domain.model;

import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import java.time.Instant;

public class Appointment {

  private final AppointmentId id;
  private final PatientId patientId;
  private final OpticalStoreId opticalStoreId;
  private TimeSlotId timeSlotId;
  private AppointmentStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  private Appointment(
      AppointmentId id,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      TimeSlotId timeSlotId,
      AppointmentStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.patientId = patientId;
    this.opticalStoreId = opticalStoreId;
    this.timeSlotId = timeSlotId;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Appointment book(
      AppointmentId id,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      TimeSlotId timeSlotId,
      Instant now) {
    return new Appointment(
        id, patientId, opticalStoreId, timeSlotId, AppointmentStatus.PENDING, now, now);
  }

  public static Appointment reconstitute(
      AppointmentId id,
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      TimeSlotId timeSlotId,
      AppointmentStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new Appointment(
        id, patientId, opticalStoreId, timeSlotId, status, createdAt, updatedAt);
  }

  public void confirm(Instant now) {
    if (status != AppointmentStatus.PENDING) {
      throw new DomainException("Only a pending appointment can be confirmed.", 409);
    }
    status = AppointmentStatus.CONFIRMED;
    updatedAt = now;
  }

  public void cancel(Instant now) {
    if (status == AppointmentStatus.CANCELLED || status == AppointmentStatus.COMPLETED) {
      throw new DomainException("This appointment can no longer be cancelled.", 409);
    }
    status = AppointmentStatus.CANCELLED;
    updatedAt = now;
  }

  public void reschedule(TimeSlotId newTimeSlotId, Instant now) {
    if (status == AppointmentStatus.CANCELLED || status == AppointmentStatus.COMPLETED) {
      throw new DomainException("This appointment can no longer be rescheduled.", 409);
    }
    this.timeSlotId = newTimeSlotId;
    this.updatedAt = now;
  }

  public boolean isAvailable() {
    return status == AppointmentStatus.PENDING || status == AppointmentStatus.CONFIRMED;
  }

  public AppointmentId id() {
    return id;
  }

  public PatientId patientId() {
    return patientId;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public TimeSlotId timeSlotId() {
    return timeSlotId;
  }

  public AppointmentStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
