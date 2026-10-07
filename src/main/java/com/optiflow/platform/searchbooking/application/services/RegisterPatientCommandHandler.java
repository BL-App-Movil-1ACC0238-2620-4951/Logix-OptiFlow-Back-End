package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.events.PatientRegistered;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.services.PatientFactory;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
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
