package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record UpdateNotificationRequest(
    @Schema(example = "true", description = "Only true is accepted.")
    @NotNull @AssertTrue Boolean read) {
}
