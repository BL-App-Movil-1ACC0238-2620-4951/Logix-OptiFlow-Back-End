package com.optiflow.platform.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterSupplierRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) String contact) {
}
