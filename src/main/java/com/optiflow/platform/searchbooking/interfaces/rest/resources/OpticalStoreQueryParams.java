package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.math.BigDecimal;

/** Query parameters for {@code GET /optical-stores}. */
public record OpticalStoreQueryParams(String name, String address, BigDecimal minRating) {}
