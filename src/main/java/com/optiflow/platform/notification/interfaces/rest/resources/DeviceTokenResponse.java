package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record DeviceTokenResponse(
    UUID id, @Schema(example = "ANDROID") String platform, Instant updatedAt) {
}
