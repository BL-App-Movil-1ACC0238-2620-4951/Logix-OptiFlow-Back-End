package com.optiflow.platform.notification.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnswerSatisfactionSurveyRequest(
    @Schema(example = "5", minimum = "1", maximum = "5") @NotNull @Min(1) @Max(5) Integer rating,
    @Schema(example = "Me atendieron rápido y los lentes quedaron perfectos.")
    @Size(max = 500) String comment) {
}
