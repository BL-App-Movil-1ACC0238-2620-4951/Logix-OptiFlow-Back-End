package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentPreferenceResponse(
    @Schema(description = "Id of the preference in Mercado Pago.") String id,
    @Schema(description = "Link that opens Checkout Pro, where the patient pays with card or "
        + "Yape.") String initPoint) {
}
