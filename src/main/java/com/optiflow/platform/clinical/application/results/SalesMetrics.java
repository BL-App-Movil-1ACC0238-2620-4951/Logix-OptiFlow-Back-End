package com.optiflow.platform.clinical.application.results;

import com.optiflow.platform.clinical.domain.model.aggregates.Sale;
import com.optiflow.platform.clinical.domain.model.entities.ElectronicReceipt;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Result of {@code GetSalesMetrics}: attentions and quotations of the patients examined in the
 * period, and the sales closed in it.
 */
public record SalesMetrics(
    LocalDate from,
    LocalDate to,
    long attentions,
    long approvedQuotations,
    long rejectedQuotations,
    List<Sale> closedSales) {

  public Money totalAmount() {
    return closedSales.stream()
        .map(SalesMetrics::amountOf)
        .reduce(Money.ZERO, Money::add);
  }

  /** Total amount divided by the closed sales; zero when no sale was closed. */
  public Money averageTicket() {
    if (closedSales.isEmpty()) {
      return Money.ZERO;
    }
    return Money.of(totalAmount().amount()
        .divide(BigDecimal.valueOf(closedSales.size()), 2, RoundingMode.HALF_UP));
  }

  public static Money amountOf(Sale sale) {
    return sale.receipt().map(ElectronicReceipt::totalAmount).orElse(Money.ZERO);
  }
}
