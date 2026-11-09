package com.optiflow.platform.production.domain.model.queries;

import java.time.LocalDate;

public record GetProductionMetricsQuery(LocalDate from, LocalDate to) {
}
