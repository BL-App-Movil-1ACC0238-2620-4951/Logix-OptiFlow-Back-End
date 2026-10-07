package com.optiflow.platform.production.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateWorkOrderStatusRequest(@NotBlank String status) {
}
