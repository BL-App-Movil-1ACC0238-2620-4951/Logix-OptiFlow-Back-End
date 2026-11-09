package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.AssignStaffRoleCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AssignStaffRoleRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromAssignStaffRoleRequestAssembler {

  public AssignStaffRoleCommand toCommand(UUID staffMemberId, AssignStaffRoleRequest request) {
    return new AssignStaffRoleCommand(
        StaffMemberId.of(staffMemberId),
        FromRegisterStaffMemberRequestAssembler.parseRole(request.role()));
  }
}
