package com.optiflow.platform.searchbooking.domain.factories;

import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
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
