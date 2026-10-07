package com.optiflow.platform.searchbooking.application.queries;

import java.math.BigDecimal;

public record FilterOpticalStoresQuery(String name, String address, BigDecimal minRating) {
}
