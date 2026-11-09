package com.optiflow.platform.searchbooking.domain.model.queries;

import java.math.BigDecimal;

public record GetNearbyOpticalStoresQuery(
    BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) implements OpticalStoresQuery {}
