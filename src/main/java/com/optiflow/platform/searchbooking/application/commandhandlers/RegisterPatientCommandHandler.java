package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.application.outboundservices.AuthenticationPort;
import com.optiflow.platform.searchbooking.domain.factories.PatientFactory;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.events.PatientRegistered;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterPatientCommandHandler {

  private final PatientRepository patientRepository;
  private final StaffMemberRepository staffMemberRepository;
  private final PatientFactory patientFactory;
  private final AuthenticationPort authenticationPort;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterPatientCommandHandler(
      PatientRepository patientRepository,
      StaffMemberRepository staffMemberRepository,
      PatientFactory patientFactory,
      AuthenticationPort authenticationPort,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.staffMemberRepository = staffMemberRepository;
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
    staffMemberRepository.findByEmail(command.email()).ifPresent(existing -> {
      throw new DomainException("This email is already registered as staff.", 409);
    });
    Instant now = clock.instant();
    Patient patient = patientFactory.create(
        command.name(),
        command.email(),
        command.phone(),
        command.birthDate().orElse(null),
        now);
    patientRepository.save(patient, authenticationPort.hash(command.password()));
    eventPublisher.publish(new PatientRegistered(patient.id(), patient.email(), now));
    return patient;
  }
}
