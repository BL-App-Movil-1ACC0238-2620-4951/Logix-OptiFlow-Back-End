package com.optiflow.platform.searchbooking.application.queries;

import java.math.BigDecimal;

public record GetNearbyOpticalStoresQuery(
    BigDecimal latitude, BigDecimal longitude, BigDecimal radiusKm) {}
