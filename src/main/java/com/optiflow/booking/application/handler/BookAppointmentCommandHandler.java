package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.BookAppointmentCommand;
import com.optiflow.booking.application.result.AppointmentDetails;
import com.optiflow.booking.domain.event.AppointmentBooked;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.exception.TimeSlotUnavailableException;
import com.optiflow.booking.domain.factory.AppointmentFactory;
import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.repository.AppointmentRepository;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
import com.optiflow.booking.domain.repository.PatientRepository;
import com.optiflow.booking.domain.repository.TimeSlotRepository;
import com.optiflow.booking.domain.service.AppointmentAvailabilityService;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookAppointmentCommandHandler {

  private final PatientRepository patientRepository;
  private final OpticalStoreRepository opticalStoreRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final AppointmentRepository appointmentRepository;
  private final AppointmentAvailabilityService availabilityService;
  private final AppointmentFactory appointmentFactory;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public BookAppointmentCommandHandler(
      PatientRepository patientRepository,
      OpticalStoreRepository opticalStoreRepository,
      TimeSlotRepository timeSlotRepository,
      AppointmentRepository appointmentRepository,
      AppointmentAvailabilityService availabilityService,
      AppointmentFactory appointmentFactory,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.opticalStoreRepository = opticalStoreRepository;
    this.timeSlotRepository = timeSlotRepository;
    this.appointmentRepository = appointmentRepository;
    this.availabilityService = availabilityService;
    this.appointmentFactory = appointmentFactory;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public AppointmentDetails handle(BookAppointmentCommand command) {
    patientRepository.findById(command.patientId())
        .orElseThrow(() -> new DomainException("Patient was not found.", 404));
    OpticalStore store = opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    if (!store.isActive()) {
      throw new DomainException("The optical store is not available.", 409);
    }
    TimeSlot timeSlot = timeSlotRepository.findByIdForUpdate(command.timeSlotId())
        .orElseThrow(() -> new DomainException("Time slot was not found.", 404));
    Instant now = clock.instant();
    availabilityService.ensureAvailable(timeSlot, now);
    if (appointmentRepository.findActiveByTimeSlot(timeSlot.id()).isPresent()) {
      throw new TimeSlotUnavailableException();
    }
    timeSlot.reserve(now);
    timeSlotRepository.save(timeSlot);
    Appointment appointment = appointmentFactory.book(
        command.patientId(), command.opticalStoreId(), timeSlot, now);
    appointment.confirm(now);
    appointmentRepository.save(appointment);
    eventPublisher.publish(new AppointmentBooked(
        appointment.id(),
        appointment.patientId(),
        appointment.opticalStoreId(),
        appointment.timeSlotId(),
        now));
    return new AppointmentDetails(appointment, timeSlot);
  }
}
