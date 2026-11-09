package com.optiflow.platform.inventory.interfaces.rest;

import com.optiflow.platform.inventory.application.services.FrameModelApplicationService;
import com.optiflow.platform.inventory.application.services.InventoryApplicationService;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameCatalogQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameModelBySkuQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromAddNewFrameModelRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromUpdateFrameModelPriceRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.StoreInventoryResponseAssembler;
import com.optiflow.platform.inventory.interfaces.rest.resources.AddNewFrameModelRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.FrameModelResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.UpdateFrameModelPriceRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.FRAME_MODELS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class FrameModelController {

  private final FrameModelApplicationService frameModelApplicationService;
  private final InventoryApplicationService inventoryApplicationService;
  private final FromAddNewFrameModelRequestAssembler addAssembler;
  private final FromUpdateFrameModelPriceRequestAssembler priceAssembler;
  private final StoreInventoryResponseAssembler responseAssembler;

  public FrameModelController(
      FrameModelApplicationService frameModelApplicationService,
      InventoryApplicationService inventoryApplicationService,
      FromAddNewFrameModelRequestAssembler addAssembler,
      FromUpdateFrameModelPriceRequestAssembler priceAssembler,
      StoreInventoryResponseAssembler responseAssembler) {
    this.frameModelApplicationService = frameModelApplicationService;
    this.inventoryApplicationService = inventoryApplicationService;
    this.addAssembler = addAssembler;
    this.priceAssembler = priceAssembler;
    this.responseAssembler = responseAssembler;
  }

  @GetMapping("/inventory/frames")
  public List<FrameModelResponse> catalog() {
    return frameModelApplicationService.catalog(new GetFrameCatalogQuery()).stream()
        .map(responseAssembler::toFrameModelResponse)
        .toList();
  }

  /** Scanned frame: its data and price, plus its stock when an optical store is given. */
  @GetMapping("/inventory/frames/{sku}")
  public FrameModelResponse get(
      @PathVariable String sku, @RequestParam(required = false) UUID opticalStoreId) {
    return responseAssembler.toFrameModelResponse(inventoryApplicationService.getFrameModel(
        new GetFrameModelBySkuQuery(
            Sku.of(sku), opticalStoreId == null ? null : OpticalStoreId.of(opticalStoreId))));
  }

  @PostMapping("/inventory/frames")
  public ResponseEntity<FrameModelResponse> add(@Valid @RequestBody AddNewFrameModelRequest request) {
    FrameModelResponse response = responseAssembler.toFrameModelResponse(
        frameModelApplicationService.add(addAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/inventory/frames/{sku}", response, response.sku());
  }

  @PatchMapping("/inventory/frames/{sku}/price")
  public FrameModelResponse updatePrice(
      @PathVariable String sku, @Valid @RequestBody UpdateFrameModelPriceRequest request) {
    return responseAssembler.toFrameModelResponse(
        frameModelApplicationService.updatePrice(priceAssembler.toCommand(sku, request)));
  }
}
