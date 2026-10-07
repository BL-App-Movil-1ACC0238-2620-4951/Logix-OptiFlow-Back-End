package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.LoginCommand;
import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.events.PatientLoggedIn;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginCommandHandler {

  private final PatientRepository patientRepository;
  private final AuthenticationPort authenticationPort;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public LoginCommandHandler(
      PatientRepository patientRepository,
      AuthenticationPort authenticationPort,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.authenticationPort = authenticationPort;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public LoginResult handle(LoginCommand command) {
    Patient patient = patientRepository.findByEmail(command.email())
        .orElseThrow(() -> new DomainException("The credentials are invalid.", 401));
    String passwordHash = patientRepository.findPasswordHashByEmail(command.email())
        .orElseThrow(() -> new DomainException("The credentials are invalid.", 401));
    if (!authenticationPort.matches(command.password(), passwordHash)) {
      throw new DomainException("The credentials are invalid.", 401);
    }
    String token = authenticationPort.issueToken(patient.id());
    eventPublisher.publish(new PatientLoggedIn(patient.id(), clock.instant()));
    return new LoginResult(token, patient);
  }
}
