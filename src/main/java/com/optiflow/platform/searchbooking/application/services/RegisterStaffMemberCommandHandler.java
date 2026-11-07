package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.RegisterStaffMemberCommand;
import com.optiflow.platform.searchbooking.domain.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.events.StaffMemberRegistered;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.StaffMemberId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterStaffMemberCommandHandler {

  private final OpticalStoreRepository opticalStoreRepository;
  private final PatientRepository patientRepository;
  private final StaffMemberRepository staffMemberRepository;
  private final AuthenticationPort authenticationPort;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RegisterStaffMemberCommandHandler(
      OpticalStoreRepository opticalStoreRepository,
      PatientRepository patientRepository,
      StaffMemberRepository staffMemberRepository,
      AuthenticationPort authenticationPort,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.patientRepository = patientRepository;
    this.staffMemberRepository = staffMemberRepository;
    this.authenticationPort = authenticationPort;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public StaffMember handle(RegisterStaffMemberCommand command) {
    opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    if (patientRepository.findByEmail(command.email()).isPresent()) {
      throw new DomainException("This email is already registered as a patient.", 409);
    }
    staffMemberRepository.findByEmail(command.email()).ifPresent(existing -> {
      throw new DomainException("A staff member with this email is already registered.", 409);
    });
    Instant now = clock.instant();
    StaffMember staffMember = StaffMember.register(
        StaffMemberId.generate(),
        command.opticalStoreId(),
        command.name(),
        command.email(),
        command.role(),
        now);
    staffMemberRepository.save(staffMember, authenticationPort.hash(command.password()));
    eventPublisher.publish(new StaffMemberRegistered(
        staffMember.id(),
        staffMember.opticalStoreId(),
        staffMember.email(),
        staffMember.role(),
        now));
    return staffMember;
  }
}
