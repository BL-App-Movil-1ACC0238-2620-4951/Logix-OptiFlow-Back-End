package com.optiflow.booking.domain.factory;

import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import java.time.Instant;

public class AppointmentFactory {

  public Appointment book(
      PatientId patientId, OpticalStoreId opticalStoreId, TimeSlot timeSlot, Instant now) {
    if (!timeSlot.opticalStoreId().equals(opticalStoreId)) {
      throw new DomainException("The time slot does not belong to the selected optical store.", 400);
    }
    return Appointment.book(
        AppointmentId.generate(), patientId, opticalStoreId, timeSlot.id(), now);
  }
}
