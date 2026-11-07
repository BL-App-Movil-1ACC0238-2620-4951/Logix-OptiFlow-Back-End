package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.AssignStaffRoleCommand;
import com.optiflow.platform.searchbooking.application.commands.RegisterStaffMemberCommand;
import com.optiflow.platform.searchbooking.application.queries.GetStaffMembersQuery;
import com.optiflow.platform.searchbooking.domain.entities.StaffMember;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StaffMemberApplicationService {

  private final RegisterStaffMemberCommandHandler registerStaffMemberCommandHandler;
  private final AssignStaffRoleCommandHandler assignStaffRoleCommandHandler;
  private final GetStaffMembersQueryService getStaffMembersQueryService;

  public StaffMemberApplicationService(
      RegisterStaffMemberCommandHandler registerStaffMemberCommandHandler,
      AssignStaffRoleCommandHandler assignStaffRoleCommandHandler,
      GetStaffMembersQueryService getStaffMembersQueryService) {
    this.registerStaffMemberCommandHandler = registerStaffMemberCommandHandler;
    this.assignStaffRoleCommandHandler = assignStaffRoleCommandHandler;
    this.getStaffMembersQueryService = getStaffMembersQueryService;
  }

  public StaffMember register(RegisterStaffMemberCommand command) {
    return registerStaffMemberCommandHandler.handle(command);
  }

  public StaffMember assignRole(AssignStaffRoleCommand command) {
    return assignStaffRoleCommandHandler.handle(command);
  }

  public List<StaffMember> find(GetStaffMembersQuery query) {
    return getStaffMembersQueryService.handle(query);
  }
}
