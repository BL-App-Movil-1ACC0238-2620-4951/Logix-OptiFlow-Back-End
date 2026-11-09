package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.queryservices.SalesMetricsQueryService;
import com.optiflow.platform.clinical.application.results.SalesMetrics;
import com.optiflow.platform.clinical.domain.model.queries.GetSalesMetricsQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.Money;
import com.optiflow.platform.clinical.interfaces.rest.resources.SalesMetricsResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Sales metrics of the business; {@code Accept: text/csv} downloads the closed sales as CSV. */
@Tag(name = OpenApiTags.SALES_REPORTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class SalesReportController {

  private static final String CSV = "text/csv";

  private final SalesMetricsQueryService salesMetricsQueryService;

  public SalesReportController(SalesMetricsQueryService salesMetricsQueryService) {
    this.salesMetricsQueryService = salesMetricsQueryService;
  }

  @Operation(summary = "Get the sales metrics",
      description = "Returns the patients examined from the start date to the end date, the "
          + "approved and rejected quotations of those examinations, the closed sales, their "
          + "total amount and the average ticket. A period without data returns zeros. Send "
          + "Accept: text/csv to download the closed sales as a CSV file.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Sales metrics, in JSON or CSV.",
          content = {
              @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SalesMetricsResponse.class)),
              @Content(mediaType = CSV, schema = @Schema(type = "string"))
          }),
      @ApiResponse(responseCode = "400",
          description = "The parameters are invalid or the start date is after the end date.")
  })
  @GetMapping(value = "/reports/sales", produces = MediaType.APPLICATION_JSON_VALUE)
  public SalesMetricsResponse metrics(
      @Parameter(description = "Start date (ISO).", example = "2026-11-01")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @Parameter(description = "End date (ISO).", example = "2026-11-30")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    SalesMetrics metrics = salesMetricsQueryService.handle(new GetSalesMetricsQuery(from, to));
    return new SalesMetricsResponse(
        metrics.from(),
        metrics.to(),
        metrics.attentions(),
        metrics.approvedQuotations(),
        metrics.rejectedQuotations(),
        metrics.closedSales().size(),
        metrics.totalAmount().amount(),
        metrics.averageTicket().amount(),
        Money.CURRENCY);
  }

  /** Same route as {@link #metrics}, which documents both formats. */
  @Hidden
  @GetMapping(value = "/reports/sales", produces = CSV)
  public ResponseEntity<String> metricsAsCsv(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    SalesMetrics metrics = salesMetricsQueryService.handle(new GetSalesMetricsQuery(from, to));
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(CSV))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"sales-" + from + "-" + to + ".csv\"")
        .body(salesMetricsQueryService.toCsv(metrics));
  }
}
