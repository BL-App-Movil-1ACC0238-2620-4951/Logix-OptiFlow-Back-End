package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.domain.model.commands.AssignStaffRoleCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.events.StaffMemberRoleAssigned;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignStaffRoleCommandHandler {

  private final StaffMemberRepository staffMemberRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public AssignStaffRoleCommandHandler(
      StaffMemberRepository staffMemberRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.staffMemberRepository = staffMemberRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public StaffMember handle(AssignStaffRoleCommand command) {
    StaffMember staffMember = staffMemberRepository.findById(command.staffMemberId())
        .orElseThrow(() -> new DomainException("Staff member was not found.", 404));
    staffMember.assignRole(command.role());
    staffMemberRepository.update(staffMember);
    Instant now = clock.instant();
    eventPublisher.publish(new StaffMemberRoleAssigned(staffMember.id(), staffMember.role(), now));
    return staffMember;
  }
}
