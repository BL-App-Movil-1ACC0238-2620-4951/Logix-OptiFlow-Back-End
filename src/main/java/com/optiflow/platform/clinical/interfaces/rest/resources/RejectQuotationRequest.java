package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectQuotationRequest(
    @NotBlank @Size(max = 255) String reason) {
}
