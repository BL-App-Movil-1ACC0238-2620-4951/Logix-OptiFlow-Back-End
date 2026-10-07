package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

  void save(Appointment appointment);

  Optional<Appointment> findById(AppointmentId id);

  List<Appointment> findByPatientId(PatientId patientId);

  Optional<Appointment> findActiveByTimeSlot(TimeSlotId timeSlotId);
}
