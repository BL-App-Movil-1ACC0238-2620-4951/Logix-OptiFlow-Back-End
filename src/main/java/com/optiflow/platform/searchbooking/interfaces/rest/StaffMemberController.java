package com.optiflow.platform.searchbooking.interfaces.rest;

import com.optiflow.platform.searchbooking.application.services.StaffMemberApplicationService;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMembersQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromAssignStaffRoleRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.FromRegisterStaffMemberRequestAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.assemblers.SearchBookingResponseAssembler;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AssignStaffRoleRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterStaffMemberRequest;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StaffMemberResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
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

  public StaffMemberController(
      StaffMemberApplicationService staffMemberApplicationService,
      FromRegisterStaffMemberRequestAssembler registerAssembler,
      FromAssignStaffRoleRequestAssembler assignRoleAssembler,
      SearchBookingResponseAssembler responseAssembler) {
    this.staffMemberApplicationService = staffMemberApplicationService;
    this.registerAssembler = registerAssembler;
    this.assignRoleAssembler = assignRoleAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/staff-members")
  public ResponseEntity<StaffMemberResponse> register(
      @Valid @RequestBody RegisterStaffMemberRequest request) {
    StaffMemberResponse response = responseAssembler.toStaffMemberResponse(
        staffMemberApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/staff-members/{id}", response, response.id());
  }

  @GetMapping("/staff-members")
  public List<StaffMemberResponse> list(@RequestParam(required = false) UUID opticalStoreId) {
    Optional<OpticalStoreId> storeId =
        opticalStoreId == null ? Optional.empty() : Optional.of(OpticalStoreId.of(opticalStoreId));
    return staffMemberApplicationService.find(new GetStaffMembersQuery(storeId)).stream()
        .map(responseAssembler::toStaffMemberResponse)
        .toList();
  }

  @PatchMapping("/staff-members/{id}/role")
  public StaffMemberResponse updateRole(
      @PathVariable UUID id, @Valid @RequestBody AssignStaffRoleRequest request) {
    return responseAssembler.toStaffMemberResponse(
        staffMemberApplicationService.assignRole(assignRoleAssembler.toCommand(id, request)));
  }
}
