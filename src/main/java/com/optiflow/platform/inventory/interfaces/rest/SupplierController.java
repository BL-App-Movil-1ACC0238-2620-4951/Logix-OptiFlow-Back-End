package com.optiflow.platform.inventory.interfaces.rest;

import com.optiflow.platform.inventory.application.services.SupplierApplicationService;
import com.optiflow.platform.inventory.domain.model.queries.GetSuppliersQuery;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.FromRegisterSupplierRequestAssembler;
import com.optiflow.platform.inventory.interfaces.rest.assemblers.StoreInventoryResponseAssembler;
import com.optiflow.platform.inventory.interfaces.rest.resources.RegisterSupplierRequest;
import com.optiflow.platform.inventory.interfaces.rest.resources.SupplierResponse;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.SUPPLIERS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class SupplierController {

  private final SupplierApplicationService supplierApplicationService;
  private final FromRegisterSupplierRequestAssembler registerAssembler;
  private final StoreInventoryResponseAssembler responseAssembler;

  public SupplierController(
      SupplierApplicationService supplierApplicationService,
      FromRegisterSupplierRequestAssembler registerAssembler,
      StoreInventoryResponseAssembler responseAssembler) {
    this.supplierApplicationService = supplierApplicationService;
    this.registerAssembler = registerAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/suppliers")
  public ResponseEntity<SupplierResponse> register(@Valid @RequestBody RegisterSupplierRequest request) {
    SupplierResponse response = responseAssembler.toSupplierResponse(
        supplierApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/suppliers", response);
  }

  @GetMapping("/suppliers")
  public List<SupplierResponse> suppliers() {
    return supplierApplicationService.suppliers(new GetSuppliersQuery()).stream()
        .map(responseAssembler::toSupplierResponse)
        .toList();
  }
}
