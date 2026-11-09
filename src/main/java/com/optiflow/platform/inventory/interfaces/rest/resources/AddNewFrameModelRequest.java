package com.optiflow.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AddNewFrameModelRequest(
    @Schema(example = "RB-2140-BLK") @NotBlank @Size(max = 100) String sku,
    @Schema(example = "Ray-Ban Wayfarer") @NotBlank @Size(max = 255) String name,
    @Schema(example = "Acetato") @Size(max = 100) String material,
    @Schema(example = "Cuadrada") @Size(max = 100) String shape,
    @Schema(example = "Negro") @Size(max = 100) String color,
    @Schema(description = "Price in PEN.", example = "320.00")
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal price,
    @Schema(description = "URL of the 3D model used by the virtual try-on.")
    @Size(max = 255) String tryOnModelUrl) {
}
