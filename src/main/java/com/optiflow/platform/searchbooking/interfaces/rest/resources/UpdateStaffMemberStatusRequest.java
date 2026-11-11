package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateStaffMemberStatusRequest(
    @Schema(description = "False deactivates the staff member, who can no longer sign in.",
        example = "false") @NotNull Boolean active) {}
