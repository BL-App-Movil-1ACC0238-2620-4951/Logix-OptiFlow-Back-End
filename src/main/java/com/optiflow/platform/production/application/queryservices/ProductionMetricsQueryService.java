package com.optiflow.platform.production.application.queryservices;

import com.optiflow.platform.production.application.results.ProductionMetrics;
import com.optiflow.platform.production.domain.model.aggregates.WorkOrder;
import com.optiflow.platform.production.domain.model.queries.GetProductionMetricsQuery;
import com.optiflow.platform.production.domain.model.valueobjects.DeliveryDelay;
import com.optiflow.platform.production.domain.model.valueobjects.StatusChange;
import com.optiflow.platform.production.domain.model.valueobjects.WorkOrderStatus;
import com.optiflow.platform.production.domain.repositories.WorkOrderRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Production metrics of the whole business for the admin reports, in JSON or exported as CSV. */
@Service
public class ProductionMetricsQueryService {

  private static final String CSV_HEADER =
      "work_order_id,status,created_at,ready_at,delivered_at,fabrication_hours,delayed";

  private final WorkOrderRepository workOrderRepository;
  private final Clock clock;

  public ProductionMetricsQueryService(WorkOrderRepository workOrderRepository, Clock clock) {
    this.workOrderRepository = workOrderRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public ProductionMetrics handle(GetProductionMetricsQuery query) {
    if (query.from().isAfter(query.to())) {
      throw new DomainException("The start date must not be after the end date.", 400);
    }
    Period period = new Period(
        query.from().atStartOfDay(clock.getZone()).toInstant(),
        query.to().plusDays(1).atStartOfDay(clock.getZone()).toInstant());
    List<WorkOrder> orders = workOrderRepository
        .findWithActivityBetween(period.from(), period.to()).stream()
        .filter(order -> period.contains(order.createdAt())
            || period.contains(order.deliveredAt())
            || readyAt(order).filter(period::contains).isPresent()
            || delayedIn(order, period))
        .toList();

    Map<WorkOrderStatus, Long> ordersByStatus = new EnumMap<>(WorkOrderStatus.class);
    for (WorkOrderStatus status : WorkOrderStatus.values()) {
      ordersByStatus.put(status, 0L);
    }
    orders.stream()
        .filter(order -> period.contains(order.createdAt()))
        .forEach(order -> ordersByStatus.merge(order.status(), 1L, Long::sum));
    List<Long> fabricationMinutes = orders.stream()
        .filter(order -> readyAt(order).filter(period::contains).isPresent())
        .map(order -> fabricationTime(order).orElseThrow().toMinutes())
        .toList();

    return new ProductionMetrics(
        query.from(),
        query.to(),
        ordersByStatus,
        orders.stream().filter(order -> period.contains(order.deliveredAt())).count(),
        averageHours(fabricationMinutes),
        orders.stream().filter(order -> delayedIn(order, period)).count(),
        orders);
  }

  /** One row per order counted in the metrics. */
  public String toCsv(ProductionMetrics metrics) {
    StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
    for (WorkOrder order : metrics.orders()) {
      csv.append(String.join(",",
              order.id().value().toString(),
              order.status().name(),
              order.createdAt().toString(),
              readyAt(order).map(Instant::toString).orElse(""),
              order.deliveredAt() == null ? "" : order.deliveredAt().toString(),
              fabricationTime(order)
                  .map(time -> averageHours(List.of(time.toMinutes())).toPlainString())
                  .orElse(""),
              String.valueOf(order.deliveryDelay().isPresent())))
          .append('\n');
    }
    return csv.toString();
  }

  private static Optional<Instant> readyAt(WorkOrder order) {
    return order.statusHistory().stream()
        .filter(change -> change.status() == WorkOrderStatus.READY_FOR_DELIVERY)
        .map(StatusChange::changedAt)
        .findFirst();
  }

  private static Optional<Duration> fabricationTime(WorkOrder order) {
    return readyAt(order).map(readyAt -> Duration.between(order.createdAt(), readyAt));
  }

  private static boolean delayedIn(WorkOrder order, Period period) {
    return order.deliveryDelay().map(DeliveryDelay::reportedAt).filter(period::contains)
        .isPresent();
  }

  private static BigDecimal averageHours(List<Long> minutes) {
    if (minutes.isEmpty()) {
      return BigDecimal.ZERO.setScale(1);
    }
    long total = minutes.stream().mapToLong(Long::longValue).sum();
    return BigDecimal.valueOf(total)
        .divide(BigDecimal.valueOf(minutes.size() * 60L), 1, RoundingMode.HALF_UP);
  }

  private record Period(Instant from, Instant to) {

    boolean contains(Instant instant) {
      return instant != null && !instant.isBefore(from) && instant.isBefore(to);
    }
  }
}
