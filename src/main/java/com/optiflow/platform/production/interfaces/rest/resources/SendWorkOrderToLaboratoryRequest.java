package com.optiflow.platform.production.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendWorkOrderToLaboratoryRequest(@NotNull UUID laboratoryId) {
}
