package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.RegisterPatientCommand;
import com.optiflow.booking.application.AuthenticationPort;
import com.optiflow.booking.domain.event.PatientRegistered;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.factory.PatientFactory;
import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.repository.PatientRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterPatientCommandHandler {

  private final PatientRepository patientRepository;
  private final PatientFactory patientFactory;
  private final AuthenticationPort authenticationPort;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterPatientCommandHandler(
      PatientRepository patientRepository,
      PatientFactory patientFactory,
      AuthenticationPort authenticationPort,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.patientFactory = patientFactory;
    this.authenticationPort = authenticationPort;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public Patient handle(RegisterPatientCommand command) {
    patientRepository.findByEmail(command.email()).ifPresent(existing -> {
      throw new DomainException("A patient with this email is already registered.", 409);
    });
    Instant now = clock.instant();
    Patient patient = patientFactory.create(command.name(), command.email(), command.phone(), now);
    patientRepository.save(patient, authenticationPort.hash(command.password()));
    eventPublisher.publish(new PatientRegistered(patient.id(), patient.email(), now));
    return patient;
  }
}
