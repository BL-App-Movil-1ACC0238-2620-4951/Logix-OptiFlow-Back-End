package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.StaffMemberApplicationService;
import com.optiflow.platform.searchbooking.domain.model.commands.UpdateStaffMemberStatusCommand;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMemberByIdQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMembersQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromAssignStaffRoleRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRegisterStaffMemberRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AssignStaffRoleRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterStaffMemberRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StaffMemberResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.UpdateStaffMemberStatusRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.StaffDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.STAFF_MEMBERS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class StaffMemberController {

  private final StaffMemberApplicationService staffMemberApplicationService;
  private final FromRegisterStaffMemberRequestAssembler registerAssembler;
  private final FromAssignStaffRoleRequestAssembler assignRoleAssembler;
  private final SearchBookingResponseAssembler responseAssembler;
  private final StaffDataAccessGuard staffDataAccessGuard;

  public StaffMemberController(
      StaffMemberApplicationService staffMemberApplicationService,
      FromRegisterStaffMemberRequestAssembler registerAssembler,
      FromAssignStaffRoleRequestAssembler assignRoleAssembler,
      SearchBookingResponseAssembler responseAssembler,
      StaffDataAccessGuard staffDataAccessGuard) {
    this.staffMemberApplicationService = staffMemberApplicationService;
    this.registerAssembler = registerAssembler;
    this.assignRoleAssembler = assignRoleAssembler;
    this.responseAssembler = responseAssembler;
    this.staffDataAccessGuard = staffDataAccessGuard;
  }

  @Operation(summary = "Register a staff member",
      description = "The admin registers an optometrist, sales advisor, technician or admin of a "
          + "store.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Staff member registered."),
      @ApiResponse(responseCode = "400", description = "The role is PATIENT."),
      @ApiResponse(responseCode = "404", description = "The optical store was not found."),
      @ApiResponse(responseCode = "409",
          description = "The email is already registered as staff or as a patient.")
  })
  @PostMapping("/staff-members")
  public ResponseEntity<StaffMemberResponse> register(
      @Valid @RequestBody RegisterStaffMemberRequest request) {
    StaffMemberResponse response = responseAssembler.toStaffMemberResponse(
        staffMemberApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/staff-members/{id}", response, response.id());
  }

  @Operation(summary = "List staff members",
      description = "Returns the staff, optionally only the members of one optical store. Only "
          + "admins.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Staff members."))
  @GetMapping("/staff-members")
  public List<StaffMemberResponse> list(@RequestParam(required = false) UUID opticalStoreId) {
    Optional<OpticalStoreId> storeId =
        opticalStoreId == null ? Optional.empty() : Optional.of(OpticalStoreId.of(opticalStoreId));
    return staffMemberApplicationService.find(new GetStaffMembersQuery(storeId)).stream()
        .map(responseAssembler::toStaffMemberResponse)
        .toList();
  }

  @Operation(summary = "Get a staff member",
      description = "Returns the profile of a staff member. Admins read any profile; the other "
          + "staff members only their own.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Staff member found."),
      @ApiResponse(responseCode = "403",
          description = "A staff member asked for another member's profile."),
      @ApiResponse(responseCode = "404", description = "The staff member was not found.")
  })
  @GetMapping("/staff-members/{id}")
  public StaffMemberResponse get(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    staffDataAccessGuard.ensureSelfStaffAccess(user, id);
    return responseAssembler.toStaffMemberResponse(
        staffMemberApplicationService.get(new GetStaffMemberByIdQuery(StaffMemberId.of(id))));
  }

  @Operation(summary = "Assign a role",
      description = "The admin changes the role of a staff member.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Role assigned."),
      @ApiResponse(responseCode = "400", description = "The role is PATIENT."),
      @ApiResponse(responseCode = "404", description = "The staff member was not found.")
  })
  @PatchMapping("/staff-members/{id}/role")
  public StaffMemberResponse updateRole(
      @PathVariable UUID id, @Valid @RequestBody AssignStaffRoleRequest request) {
    return responseAssembler.toStaffMemberResponse(
        staffMemberApplicationService.assignRole(assignRoleAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Activate or deactivate a staff member",
      description = "The admin deactivates a staff member, who can no longer sign in, or activates "
          + "them again. Admins cannot deactivate their own account.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Status updated."),
      @ApiResponse(responseCode = "400", description = "The admin tried to deactivate themselves."),
      @ApiResponse(responseCode = "404", description = "The staff member was not found.")
  })
  @PatchMapping("/staff-members/{id}/status")
  public StaffMemberResponse updateStatus(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateStaffMemberStatusRequest request,
      @AuthenticationPrincipal AuthenticatedUser user) {
    if (!request.active() && id.equals(user.userId())) {
      throw new DomainException("You cannot deactivate your own account.", 400);
    }
    return responseAssembler.toStaffMemberResponse(staffMemberApplicationService.updateStatus(
        new UpdateStaffMemberStatusCommand(StaffMemberId.of(id), request.active())));
  }
}
