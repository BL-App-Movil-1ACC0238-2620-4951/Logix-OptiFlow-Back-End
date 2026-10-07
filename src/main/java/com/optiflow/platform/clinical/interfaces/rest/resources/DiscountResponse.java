package com.optiflow.platform.clinical.interfaces.rest.resources;

import java.math.BigDecimal;

public record DiscountResponse(
    String type,
    BigDecimal value,
    String reason,
    BigDecimal amount) {
}
