package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateStaffMemberRoleRequest(@NotBlank String role) {}
