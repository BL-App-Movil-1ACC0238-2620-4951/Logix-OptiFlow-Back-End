package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.query.GetAppointmentQuery;
import com.optiflow.booking.application.result.AppointmentDetails;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.repository.AppointmentRepository;
import com.optiflow.booking.domain.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAppointmentQueryService {

  private final AppointmentRepository appointmentRepository;
  private final TimeSlotRepository timeSlotRepository;

  public GetAppointmentQueryService(
      AppointmentRepository appointmentRepository, TimeSlotRepository timeSlotRepository) {
    this.appointmentRepository = appointmentRepository;
    this.timeSlotRepository = timeSlotRepository;
  }

  @Transactional(readOnly = true)
  public AppointmentDetails handle(GetAppointmentQuery query) {
    Appointment appointment = appointmentRepository.findById(query.appointmentId())
        .orElseThrow(() -> new DomainException("Appointment was not found.", 404));
    return timeSlotRepository.findById(appointment.timeSlotId())
        .map(slot -> new AppointmentDetails(appointment, slot))
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
  }
}
