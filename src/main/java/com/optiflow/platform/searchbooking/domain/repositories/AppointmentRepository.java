package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.TimeSlotId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

  void save(Appointment appointment);

  Optional<Appointment> findById(AppointmentId id);

  List<Appointment> findByPatientId(PatientId patientId);

  Optional<Appointment> findActiveByTimeSlot(TimeSlotId timeSlotId);

  List<Appointment> findByOpticalStoreAndTimeSlotStartBetween(
      OpticalStoreId opticalStoreId, Instant startInclusive, Instant endExclusive);
}
