package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.application.outboundservices.AuthenticationPort;
import com.optiflow.platform.searchbooking.application.results.LoginResult;
import com.optiflow.platform.searchbooking.domain.model.commands.LoginCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.events.PatientLoggedIn;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.infrastructure.security.IssuedJwt;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginCommandHandler {

  private final PatientRepository patientRepository;
  private final StaffMemberRepository staffMemberRepository;
  private final AuthenticationPort authenticationPort;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public LoginCommandHandler(
      PatientRepository patientRepository,
      StaffMemberRepository staffMemberRepository,
      AuthenticationPort authenticationPort,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.staffMemberRepository = staffMemberRepository;
    this.authenticationPort = authenticationPort;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public LoginResult handle(LoginCommand command) {
    return patientRepository.findByEmail(command.email())
        .map(patient -> authenticatePatient(command, patient))
        .orElseGet(() -> staffMemberRepository.findByEmail(command.email())
            .map(staff -> authenticateStaff(command, staff))
            .orElseThrow(() -> new DomainException("The credentials are invalid.", 401)));
  }

  private LoginResult authenticatePatient(LoginCommand command, Patient patient) {
    String passwordHash = patientRepository.findPasswordHashByEmail(command.email())
        .orElseThrow(() -> new DomainException("The credentials are invalid.", 401));
    if (!authenticationPort.matches(command.password(), passwordHash)) {
      throw new DomainException("The credentials are invalid.", 401);
    }
    IssuedJwt issued = authenticationPort.issueToken(patient.id().value(), "PATIENT");
    eventPublisher.publish(new PatientLoggedIn(patient.id(), clock.instant()));
    return new LoginResult(
        issued.token(), issued.expiresAt(), patient.id().value(), "PATIENT");
  }

  private LoginResult authenticateStaff(LoginCommand command, StaffMember staffMember) {
    if (!staffMember.active()) {
      throw new DomainException("The credentials are invalid.", 401);
    }
    String passwordHash = staffMemberRepository.findPasswordHashByEmail(command.email())
        .orElseThrow(() -> new DomainException("The credentials are invalid.", 401));
    if (!authenticationPort.matches(command.password(), passwordHash)) {
      throw new DomainException("The credentials are invalid.", 401);
    }
    String role = staffMember.role().name();
    IssuedJwt issued = authenticationPort.issueToken(staffMember.id().value(), role);
    return new LoginResult(issued.token(), issued.expiresAt(), staffMember.id().value(), role);
  }
}
