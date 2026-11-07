package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetAppointmentsByOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.repositories.AppointmentRepository;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAppointmentsByOpticalStoreQueryService {

  private final OpticalStoreRepository opticalStoreRepository;
  private final AppointmentRepository appointmentRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final Clock clock;

  public GetAppointmentsByOpticalStoreQueryService(
      OpticalStoreRepository opticalStoreRepository,
      AppointmentRepository appointmentRepository,
      TimeSlotRepository timeSlotRepository,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.appointmentRepository = appointmentRepository;
    this.timeSlotRepository = timeSlotRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public List<AppointmentDetails> handle(GetAppointmentsByOpticalStoreQuery query) {
    opticalStoreRepository.findById(query.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    Instant dayStart = query.date().atStartOfDay(clock.getZone()).toInstant();
    Instant dayEnd = query.date().plusDays(1).atStartOfDay(clock.getZone()).toInstant();
    return appointmentRepository
        .findByOpticalStoreAndTimeSlotStartBetween(query.opticalStoreId(), dayStart, dayEnd)
        .stream()
        .map(this::withSlot)
        .toList();
  }

  private AppointmentDetails withSlot(Appointment appointment) {
    return timeSlotRepository.findById(appointment.timeSlotId())
        .map(slot -> new AppointmentDetails(appointment, slot))
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
  }
}
