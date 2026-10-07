package com.optiflow.booking.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SaveFavoriteStoreRequest(@NotNull UUID opticalStoreId) {
}
