package com.optiflow.platform.production.application.results;

import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Result of {@code GetProductionMetrics}.
 *
 * @param ordersByStatus current status of the orders created in the period, every status included
 * @param averageFabricationHours hours from the creation of an order until it was ready for
 *     delivery, for the orders that became ready in the period; zero when none did
 * @param orders every order counted in one of the metrics
 */
public record ProductionMetrics(
    LocalDate from,
    LocalDate to,
    Map<WorkOrderStatus, Long> ordersByStatus,
    long deliveredOrders,
    BigDecimal averageFabricationHours,
    long delayedOrders,
    List<WorkOrder> orders) {
}
