package com.optiflow.platform.clinical.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesMetricsResponse(
    @Schema(example = "2026-11-01") LocalDate from,
    @Schema(example = "2026-11-30") LocalDate to,
    @Schema(description = "Patients examined in the period.", example = "42") long attentions,
    @Schema(description = "Approved quotations of those examinations.", example = "30")
    long approvedQuotations,
    @Schema(description = "Rejected quotations of those examinations.", example = "5")
    long rejectedQuotations,
    @Schema(description = "Sales closed in the period.", example = "28") long closedSales,
    @Schema(description = "Amount of the receipts of the closed sales.", example = "12323.80")
    BigDecimal totalAmount,
    @Schema(description = "Total amount divided by the closed sales.", example = "440.14")
    BigDecimal averageTicket,
    @Schema(example = "PEN") String currency) {
}
