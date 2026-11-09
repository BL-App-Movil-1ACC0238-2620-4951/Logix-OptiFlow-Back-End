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

  @GetMapping(value = "/reports/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public InventoryMetricsResponse metrics(
      @RequestParam UUID opticalStoreId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    return responseAssembler.toInventoryMetricsResponse(
        inventoryApplicationService.metrics(query(opticalStoreId, from, to)));
  }

  @GetMapping(value = "/reports/inventory", produces = CSV)
  public ResponseEntity<String> metricsAsCsv(
      @RequestParam UUID opticalStoreId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
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
