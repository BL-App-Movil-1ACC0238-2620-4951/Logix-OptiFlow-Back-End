package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

  void save(Appointment appointment);

  Optional<Appointment> findById(AppointmentId id);

  List<Appointment> findByPatientId(PatientId patientId);

  Optional<Appointment> findActiveByTimeSlot(TimeSlotId timeSlotId);
}
