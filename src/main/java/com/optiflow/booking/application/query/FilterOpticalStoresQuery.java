package com.optiflow.booking.application.query;

import java.math.BigDecimal;

public record FilterOpticalStoresQuery(String name, String address, BigDecimal minRating) {
}
