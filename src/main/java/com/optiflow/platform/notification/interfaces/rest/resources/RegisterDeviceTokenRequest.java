package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDeviceTokenRequest(
    @Schema(example = "fcm-registration-token", description = "Firebase Cloud Messaging token.")
    @NotBlank @Size(max = 512) String token,
    @Schema(example = "ANDROID", allowableValues = "ANDROID")
    @NotBlank @Pattern(regexp = "ANDROID") String platform) {
}
