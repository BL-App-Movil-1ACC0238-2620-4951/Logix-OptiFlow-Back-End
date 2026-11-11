package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commandhandlers.AssignStaffRoleCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.RegisterStaffMemberCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.UpdateStaffMemberStatusCommandHandler;
import com.optiflow.platform.searchbooking.application.queryservices.GetStaffMemberByIdQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.GetStaffMembersQueryService;
import com.optiflow.platform.searchbooking.domain.model.commands.AssignStaffRoleCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterStaffMemberCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.UpdateStaffMemberStatusCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMemberByIdQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMembersQuery;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StaffMemberApplicationService {

  private final RegisterStaffMemberCommandHandler registerStaffMemberCommandHandler;
  private final AssignStaffRoleCommandHandler assignStaffRoleCommandHandler;
  private final UpdateStaffMemberStatusCommandHandler updateStaffMemberStatusCommandHandler;
  private final GetStaffMembersQueryService getStaffMembersQueryService;
  private final GetStaffMemberByIdQueryService getStaffMemberByIdQueryService;

  public StaffMemberApplicationService(
      RegisterStaffMemberCommandHandler registerStaffMemberCommandHandler,
      AssignStaffRoleCommandHandler assignStaffRoleCommandHandler,
      UpdateStaffMemberStatusCommandHandler updateStaffMemberStatusCommandHandler,
      GetStaffMembersQueryService getStaffMembersQueryService,
      GetStaffMemberByIdQueryService getStaffMemberByIdQueryService) {
    this.registerStaffMemberCommandHandler = registerStaffMemberCommandHandler;
    this.assignStaffRoleCommandHandler = assignStaffRoleCommandHandler;
    this.updateStaffMemberStatusCommandHandler = updateStaffMemberStatusCommandHandler;
    this.getStaffMembersQueryService = getStaffMembersQueryService;
    this.getStaffMemberByIdQueryService = getStaffMemberByIdQueryService;
  }

  public StaffMember register(RegisterStaffMemberCommand command) {
    return registerStaffMemberCommandHandler.handle(command);
  }

  public StaffMember assignRole(AssignStaffRoleCommand command) {
    return assignStaffRoleCommandHandler.handle(command);
  }

  public StaffMember updateStatus(UpdateStaffMemberStatusCommand command) {
    return updateStaffMemberStatusCommandHandler.handle(command);
  }

  public List<StaffMember> find(GetStaffMembersQuery query) {
    return getStaffMembersQueryService.handle(query);
  }

  public StaffMember get(GetStaffMemberByIdQuery query) {
    return getStaffMemberByIdQueryService.handle(query);
  }
}
