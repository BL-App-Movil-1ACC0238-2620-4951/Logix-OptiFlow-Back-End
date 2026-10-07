package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record QuotationItemRequest(
    @NotBlank String itemType,
    @Size(max = 60) String productSku,
    @NotBlank @Size(max = 255) String description,
    @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal unitPrice,
    @NotNull @Min(1) @Max(100) Integer quantity) {
}
