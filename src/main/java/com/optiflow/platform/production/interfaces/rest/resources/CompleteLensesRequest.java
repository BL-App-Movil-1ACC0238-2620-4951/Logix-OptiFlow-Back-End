package com.optiflow.platform.production.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CompleteLensesRequest(@Size(max = 10) List<@NotNull UUID> lensIds) {
}
