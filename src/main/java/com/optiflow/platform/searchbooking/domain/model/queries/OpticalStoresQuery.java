package com.optiflow.platform.searchbooking.domain.model.queries;

/** Query that lists optical stores from the parameters of {@code GET /optical-stores}. */
public sealed interface OpticalStoresQuery
    permits SearchOpticalStoresQuery, FilterOpticalStoresQuery, GetNearbyOpticalStoresQuery {}
