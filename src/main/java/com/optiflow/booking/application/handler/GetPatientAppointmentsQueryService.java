package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.query.GetPatientAppointmentsQuery;
import com.optiflow.booking.application.result.AppointmentDetails;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.repository.AppointmentRepository;
import com.optiflow.booking.domain.repository.PatientRepository;
import com.optiflow.booking.domain.repository.TimeSlotRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPatientAppointmentsQueryService {

  private final PatientRepository patientRepository;
  private final AppointmentRepository appointmentRepository;
  private final TimeSlotRepository timeSlotRepository;

  public GetPatientAppointmentsQueryService(
      PatientRepository patientRepository,
      AppointmentRepository appointmentRepository,
      TimeSlotRepository timeSlotRepository) {
    this.patientRepository = patientRepository;
    this.appointmentRepository = appointmentRepository;
    this.timeSlotRepository = timeSlotRepository;
  }

  @Transactional(readOnly = true)
  public List<AppointmentDetails> handle(GetPatientAppointmentsQuery query) {
    patientRepository.findById(query.patientId())
        .orElseThrow(() -> new DomainException("Patient was not found.", 404));
    return appointmentRepository.findByPatientId(query.patientId()).stream()
        .map(this::withSlot)
        .toList();
  }

  private AppointmentDetails withSlot(Appointment appointment) {
    return timeSlotRepository.findById(appointment.timeSlotId())
        .map(slot -> new AppointmentDetails(appointment, slot))
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
  }
}
