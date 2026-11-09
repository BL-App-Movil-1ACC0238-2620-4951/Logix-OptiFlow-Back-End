package com.optiflow.platform.production.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ProductionMetricsResponse(
    @Schema(example = "2026-11-01") LocalDate from,
    @Schema(example = "2026-11-30") LocalDate to,
    @Schema(description = "Current status of the orders created in the period.",
        example = "{\"PENDING\":2,\"IN_WORKSHOP\":3,\"QUALITY_CONTROL\":1,"
            + "\"READY_FOR_DELIVERY\":4,\"DELIVERED\":18}")
    Map<String, Long> ordersByStatus,
    @Schema(description = "Orders delivered in the period.", example = "20") long deliveredOrders,
    @Schema(description = "Average hours from the creation of an order until it was ready for "
        + "delivery, for the orders that became ready in the period.", example = "52.5")
    BigDecimal averageFabricationHours,
    @Schema(description = "Orders whose delay was reported in the period.", example = "3")
    long delayedOrders) {
}
