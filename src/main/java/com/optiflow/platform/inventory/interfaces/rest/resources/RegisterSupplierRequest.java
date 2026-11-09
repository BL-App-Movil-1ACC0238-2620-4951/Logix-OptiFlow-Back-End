package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterSupplierRequest(
    @Schema(example = "Distribuidora Andina") @NotBlank @Size(max = 255) String name,
    @Schema(example = "ventas@andina.pe") @Size(max = 255) String contact) {
}
