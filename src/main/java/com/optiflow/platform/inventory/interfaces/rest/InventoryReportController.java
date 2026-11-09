package com.optiflow.platform.inventory.interfaces.rest;

import com.optiflow.platform.inventory.application.queryservices.InventoryMetricsQueryService;
import com.optiflow.platform.inventory.application.results.InventoryMetrics;
import com.optiflow.platform.inventory.application.services.InventoryApplicationService;
import com.optiflow.platform.inventory.domain.model.queries.GetInventoryMetricsQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.StoreInventoryResponseAssembler;
import com.optiflow.platform.inventory.interfaces.rest.resources.InventoryMetricsResponse;
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
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Inventory metrics of a store; {@code Accept: text/csv} downloads them as CSV. */
@Tag(name = OpenApiTags.INVENTORY_REPORTS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class InventoryReportController {

  private static final String CSV = "text/csv";

  private final InventoryApplicationService inventoryApplicationService;
  private final InventoryMetricsQueryService inventoryMetricsQueryService;
  private final StoreInventoryResponseAssembler responseAssembler;

  public InventoryReportController(
      InventoryApplicationService inventoryApplicationService,
      InventoryMetricsQueryService inventoryMetricsQueryService,
      StoreInventoryResponseAssembler responseAssembler) {
    this.inventoryApplicationService = inventoryApplicationService;
    this.inventoryMetricsQueryService = inventoryMetricsQueryService;
    this.responseAssembler = responseAssembler;
  }

  @Operation(summary = "Get the inventory metrics of a store",
      description = "Returns the units that came in and went out from the start date to the end "
          + "date, the number of low stock products and the detail of each frame. Send "
          + "Accept: text/csv to download the same detail as a CSV file.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Inventory metrics, in JSON or CSV.",
          content = {
              @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = InventoryMetricsResponse.class)),
              @Content(mediaType = CSV, schema = @Schema(type = "string"))
          }),
      @ApiResponse(responseCode = "400",
          description = "The parameters are invalid or the start date is after the end date."),
      @ApiResponse(responseCode = "404", description = "The optical store has no inventory.")
  })
  @GetMapping(value = "/reports/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public InventoryMetricsResponse metrics(
      @Parameter(description = "Optical store of the report.",
          example = "11111111-1111-1111-1111-111111111111")
      @RequestParam UUID opticalStoreId,
      @Parameter(description = "Start date (ISO).", example = "2026-11-01")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @Parameter(description = "End date (ISO).", example = "2026-11-30")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return responseAssembler.toInventoryMetricsResponse(
        inventoryApplicationService.metrics(query(opticalStoreId, from, to)));
  }

  /** Same route as {@link #metrics}, which documents both formats. */
  @Hidden
  @GetMapping(value = "/reports/inventory", produces = CSV)
  public ResponseEntity<String> metricsAsCsv(
      @Parameter(description = "Optical store of the report.",
          example = "11111111-1111-1111-1111-111111111111")
      @RequestParam UUID opticalStoreId,
      @Parameter(description = "Start date (ISO).", example = "2026-11-01")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @Parameter(description = "End date (ISO).", example = "2026-11-30")
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    InventoryMetrics metrics = inventoryApplicationService.metrics(query(opticalStoreId, from, to));
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(CSV))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"inventory-" + from + "-" + to + ".csv\"")
        .body(inventoryMetricsQueryService.toCsv(metrics));
  }

  private static GetInventoryMetricsQuery query(UUID opticalStoreId, LocalDate from, LocalDate to) {
    return new GetInventoryMetricsQuery(OpticalStoreId.of(opticalStoreId), from, to);
  }
}
