package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetAppointmentQuery;
import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.repositories.AppointmentRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.exceptions.DomainException;
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
