package com.optiflow.platform.inventory.interfaces.rest;

import com.optiflow.platform.inventory.application.services.FrameModelApplicationService;
import com.optiflow.platform.inventory.application.services.InventoryApplicationService;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameCatalogQuery;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameModelBySkuQuery;
import com.optiflow.platform.inventory.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromAddNewFrameModelRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromUpdateFrameModelPriceRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromUpdateFrameModelTryOnModelRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.StoreInventoryResponseAssembler;
import com.optiflow.platform.inventory.interfaces.rest.resources.AddNewFrameModelRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.FrameModelResponse;
import com.optiflow.platform.inventory.interfaces.rest.resources.UpdateFrameModelPriceRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.UpdateFrameModelTryOnModelRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
  private final FromUpdateFrameModelTryOnModelRequestAssembler tryOnModelAssembler;
  private final StoreInventoryResponseAssembler responseAssembler;

  public FrameModelController(
      FrameModelApplicationService frameModelApplicationService,
      InventoryApplicationService inventoryApplicationService,
      FromAddNewFrameModelRequestAssembler addAssembler,
      FromUpdateFrameModelPriceRequestAssembler priceAssembler,
      FromUpdateFrameModelTryOnModelRequestAssembler tryOnModelAssembler,
      StoreInventoryResponseAssembler responseAssembler) {
    this.frameModelApplicationService = frameModelApplicationService;
    this.inventoryApplicationService = inventoryApplicationService;
    this.addAssembler = addAssembler;
    this.priceAssembler = priceAssembler;
    this.tryOnModelAssembler = tryOnModelAssembler;
    this.responseAssembler = responseAssembler;
  }

  @Operation(summary = "List the frame catalog",
      description = "Returns every frame model ordered by name, with its SKU, price and the 3D "
          + "model of the virtual try-on.")
  @ApiResponses(@ApiResponse(responseCode = "200", description = "Frame catalog."))
  @GetMapping("/inventory/frames")
  public List<FrameModelResponse> catalog() {
    return frameModelApplicationService.catalog(new GetFrameCatalogQuery()).stream()
        .map(responseAssembler::toFrameModelResponse)
        .toList();
  }

  /** Scanned frame: its data and price, plus its stock when an optical store is given. */
  @Operation(summary = "Get a scanned frame",
      description = "Returns the data and price of the frame with this SKU. When opticalStoreId "
          + "is given, it also returns its quantity and minimum stock in that store.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Frame found."),
      @ApiResponse(responseCode = "404", description = "No frame model has this SKU.")
  })
  @GetMapping("/inventory/frames/{sku}")
  public FrameModelResponse get(
      @Parameter(description = "SKU read from the frame label.", example = "RB-2140-BLK")
      @PathVariable String sku,
      @Parameter(description = "Optical store whose stock of the frame is included.",
          example = "11111111-1111-1111-1111-111111111111")
      @RequestParam(required = false) UUID opticalStoreId) {
    return responseAssembler.toFrameModelResponse(inventoryApplicationService.getFrameModel(
        new GetFrameModelBySkuQuery(
            Sku.of(sku), opticalStoreId == null ? null : OpticalStoreId.of(opticalStoreId))));
  }

  @Operation(summary = "Add a frame model",
      description = "Registers a new frame in the catalog. Its price is in PEN.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "Frame model created. The Location header points to it."),
      @ApiResponse(responseCode = "409",
          description = "A frame model with this SKU already exists.")
  })
  @PostMapping("/inventory/frames")
  public ResponseEntity<FrameModelResponse> add(@Valid @RequestBody AddNewFrameModelRequest request) {
    FrameModelResponse response = responseAssembler.toFrameModelResponse(
        frameModelApplicationService.add(addAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/inventory/frames/{sku}", response, response.sku());
  }

  @Operation(summary = "Update the price of a frame")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Frame model with its new price."),
      @ApiResponse(responseCode = "404", description = "No frame model has this SKU.")
  })
  @PatchMapping("/inventory/frames/{sku}/price")
  public FrameModelResponse updatePrice(
      @Parameter(description = "SKU of the frame.", example = "RB-2140-BLK") @PathVariable String sku,
      @Valid @RequestBody UpdateFrameModelPriceRequest request) {
    return responseAssembler.toFrameModelResponse(
        frameModelApplicationService.updatePrice(priceAssembler.toCommand(sku, request)));
  }

  @Operation(summary = "Update the virtual try-on model of a frame",
      description = "Assigns the model that the app uses to show the frame on the patient's face. "
          + "An empty or null value removes it, and the app then hides the try-on option.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Frame model with its try-on model."),
      @ApiResponse(responseCode = "404", description = "No frame model has this SKU.")
  })
  @PatchMapping("/inventory/frames/{sku}/try-on-model")
  public FrameModelResponse updateTryOnModel(
      @Parameter(description = "SKU of the frame.", example = "RB-2140-BLK") @PathVariable String sku,
      @Valid @RequestBody UpdateFrameModelTryOnModelRequest request) {
    return responseAssembler.toFrameModelResponse(frameModelApplicationService.updateTryOnModel(
        tryOnModelAssembler.toCommand(sku, request)));
  }
}
