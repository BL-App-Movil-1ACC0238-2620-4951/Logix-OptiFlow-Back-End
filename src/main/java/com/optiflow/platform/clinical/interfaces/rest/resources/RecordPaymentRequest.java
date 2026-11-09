package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Cash payment registered by the sales advisor; card and Yape arrive from the gateway. */
public record RecordPaymentRequest(
    @Schema(description = "Amount received in cash; it must match the quotation total.",
        example = "440.10")
    @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal amount) {
}
