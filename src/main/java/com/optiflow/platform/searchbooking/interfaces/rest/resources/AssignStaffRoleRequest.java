package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record AssignStaffRoleRequest(@NotBlank String role) {}
