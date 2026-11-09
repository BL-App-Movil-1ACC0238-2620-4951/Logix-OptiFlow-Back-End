package com.optiflow.platform.searchbooking.domain.model.queries;

import java.math.BigDecimal;

public record FilterOpticalStoresQuery(String name, String address, BigDecimal minRating) {
}
