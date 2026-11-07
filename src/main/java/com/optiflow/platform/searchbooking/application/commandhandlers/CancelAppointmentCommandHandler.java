package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.application.results.AppointmentDetails;
import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.commands.CancelAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.model.events.AppointmentCancelled;
import com.optiflow.platform.searchbooking.domain.repositories.AppointmentRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelAppointmentCommandHandler {

  private final AppointmentRepository appointmentRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public CancelAppointmentCommandHandler(
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
  public AppointmentDetails handle(CancelAppointmentCommand command) {
    Appointment appointment = appointmentRepository.findById(command.appointmentId())
        .orElseThrow(() -> new DomainException("Appointment was not found.", 404));
    TimeSlot timeSlot = timeSlotRepository.findByIdForUpdate(appointment.timeSlotId())
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
    Instant now = clock.instant();
    appointment.cancel(now);
    timeSlot.release();
    appointmentRepository.save(appointment);
    timeSlotRepository.save(timeSlot);
    eventPublisher.publish(new AppointmentCancelled(
        appointment.id(),
        appointment.patientId(),
        appointment.opticalStoreId(),
        appointment.timeSlotId(),
        now));
    return new AppointmentDetails(appointment, timeSlot);
  }
}
