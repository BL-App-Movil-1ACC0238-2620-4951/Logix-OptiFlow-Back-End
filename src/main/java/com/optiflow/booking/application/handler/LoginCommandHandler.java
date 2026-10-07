package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.AuthenticationPort;
import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.LoginCommand;
import com.optiflow.booking.application.result.LoginResult;
import com.optiflow.booking.domain.event.PatientLoggedIn;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.Patient;
import com.optiflow.booking.domain.repository.PatientRepository;
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
