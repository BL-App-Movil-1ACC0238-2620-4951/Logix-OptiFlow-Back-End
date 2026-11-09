package com.optiflow.platform.production.interfaces.rest;

import com.optiflow.platform.production.application.queryservices.ProductionMetricsQueryService;
import com.optiflow.platform.production.application.results.ProductionMetrics;
import com.optiflow.platform.production.domain.model.queries.GetProductionMetricsQuery;
import com.optiflow.platform.production.interfaces.rest.resources.ProductionMetricsResponse;
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
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Production metrics of the business; {@code Accept: text/csv} downloads the orders as CSV. */
@Tag(name = OpenApiTags.PRODUCTION_REPORTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class ProductionReportController {

  private static final String CSV = "text/csv";

  private final ProductionMetricsQueryService productionMetricsQueryService;

  public ProductionReportController(ProductionMetricsQueryService productionMetricsQueryService) {
    this.productionMetricsQueryService = productionMetricsQueryService;
  }

  @Operation(summary = "Get the production metrics",
      description = "Returns, from the start date to the end date, the current status of the "
          + "orders created, the orders delivered, the average fabrication time and the orders "
          + "delayed. A period without data returns zeros. Send Accept: text/csv to download "
          + "the orders of the period as a CSV file.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Production metrics, in JSON or CSV.",
          content = {
              @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ProductionMetricsResponse.class)),
              @Content(mediaType = CSV, schema = @Schema(type = "string"))
          }),
      @ApiResponse(responseCode = "400",
          description = "The parameters are invalid or the start date is after the end date.")
  })
  @GetMapping(value = "/reports/production", produces = MediaType.APPLICATION_JSON_VALUE)
  public ProductionMetricsResponse metrics(
      @Parameter(description = "Start date (ISO).", example = "2026-11-01")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @Parameter(description = "End date (ISO).", example = "2026-11-30")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    ProductionMetrics metrics =
        productionMetricsQueryService.handle(new GetProductionMetricsQuery(from, to));
    Map<String, Long> ordersByStatus = new LinkedHashMap<>();
    metrics.ordersByStatus().forEach((status, count) -> ordersByStatus.put(status.name(), count));
    return new ProductionMetricsResponse(
        metrics.from(),
        metrics.to(),
        ordersByStatus,
        metrics.deliveredOrders(),
        metrics.averageFabricationHours(),
        metrics.delayedOrders());
  }

  /** Same route as {@link #metrics}, which documents both formats. */
  @Hidden
  @GetMapping(value = "/reports/production", produces = CSV)
  public ResponseEntity<String> metricsAsCsv(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    ProductionMetrics metrics =
        productionMetricsQueryService.handle(new GetProductionMetricsQuery(from, to));
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(CSV))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"production-" + from + "-" + to + ".csv\"")
        .body(productionMetricsQueryService.toCsv(metrics));
  }
}
