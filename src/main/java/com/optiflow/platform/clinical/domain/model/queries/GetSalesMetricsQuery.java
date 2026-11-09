package com.optiflow.platform.clinical.domain.model.queries;

import java.time.LocalDate;

public record GetSalesMetricsQuery(LocalDate from, LocalDate to) {
}
