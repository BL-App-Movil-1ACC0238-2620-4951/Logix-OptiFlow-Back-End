package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SaveFavoriteStoreRequest(@NotNull UUID opticalStoreId) {
}
