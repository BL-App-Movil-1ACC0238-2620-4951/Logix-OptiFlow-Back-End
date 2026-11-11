package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateFrameModelTryOnModelRequest(
    @Schema(description = "Virtual try-on model of the frame. Empty or null removes it.",
        example = "try-on/aviator")
    @Size(max = 255) String tryOnModelUrl) {
}
