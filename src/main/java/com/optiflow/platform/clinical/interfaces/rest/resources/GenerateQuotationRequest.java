package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record GenerateQuotationRequest(
    @NotNull UUID clinicalRecordId,
    @NotEmpty @Size(max = 20) List<@Valid @NotNull QuotationItemRequest> items) {
}
