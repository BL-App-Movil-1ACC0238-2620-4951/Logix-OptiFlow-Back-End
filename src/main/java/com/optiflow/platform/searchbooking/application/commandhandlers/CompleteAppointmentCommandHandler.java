package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.domain.model.commands.CompleteAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCompleted;
import com.optiflow.platform.searchbooking.domain.repositories.AppointmentRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;

@Service
public class CompleteAppointmentCommandHandler {

  private final AppointmentRepository appointmentRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public CompleteAppointmentCommandHandler(
      AppointmentRepository appointmentRepository,
      TimeSlotRepository timeSlotRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.appointmentRepository = appointmentRepository;
    this.timeSlotRepository = timeSlotRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public AppointmentDetails handle(CompleteAppointmentCommand command) {
    Appointment appointment = appointmentRepository.findById(command.appointmentId())
        .orElseThrow(() -> new DomainException("Appointment was not found.", 404));
    Instant now = clock.instant();
    appointment.complete(now);
    appointmentRepository.save(appointment);
    eventPublisher.publish(new AppointmentCompleted(
        appointment.id(),
        appointment.patientId(),
        appointment.opticalStoreId(),
        appointment.timeSlotId(),
        now));
    TimeSlot timeSlot = timeSlotRepository.findById(appointment.timeSlotId())
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
    return new AppointmentDetails(appointment, timeSlot);
  }
}
