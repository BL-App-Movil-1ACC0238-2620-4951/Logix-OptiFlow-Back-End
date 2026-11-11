package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.domain.model.commands.UpdateStaffMemberStatusCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Activates or deactivates a staff member; an inactive member cannot sign in. */
@Service
public class UpdateStaffMemberStatusCommandHandler {

  private final StaffMemberRepository staffMemberRepository;

  public UpdateStaffMemberStatusCommandHandler(StaffMemberRepository staffMemberRepository) {
    this.staffMemberRepository = staffMemberRepository;
  }

  @Transactional
  public StaffMember handle(UpdateStaffMemberStatusCommand command) {
    StaffMember staffMember = staffMemberRepository.findById(command.staffMemberId())
        .orElseThrow(() -> new DomainException("Staff member was not found.", 404));
    if (command.active()) {
      staffMember.activate();
    } else {
      staffMember.deactivate();
    }
    staffMemberRepository.update(staffMember);
    return staffMember;
  }
}
