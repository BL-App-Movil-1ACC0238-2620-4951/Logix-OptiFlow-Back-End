package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterStaffMemberRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotNull UUID opticalStoreId,
    @NotBlank String role) {}
