package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.services.SaleApplicationService;
import com.optiflow.platform.clinical.domain.model.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.model.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRecordPaymentRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordPaymentRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterSaleRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.SaleResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.UpdateSaleStatusRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.SALES)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class SaleController {

  private final SaleApplicationService saleApplicationService;
  private final FromRecordPaymentRequestAssembler paymentAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;

  public SaleController(
      SaleApplicationService saleApplicationService,
      FromRecordPaymentRequestAssembler paymentAssembler,
      ClinicalCommercialResponseAssembler responseAssembler) {
    this.saleApplicationService = saleApplicationService;
    this.paymentAssembler = paymentAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/sales")
  public ResponseEntity<SaleResponse> register(@Valid @RequestBody RegisterSaleRequest request) {
    SaleResponse response = responseAssembler.toSaleResponse(saleApplicationService.register(
        new RegisterSaleCommand(QuotationId.of(request.quotationId()))));
    return CreatedResourceResponses.at("/sales/{id}", response, response.id());
  }

  @GetMapping("/sales/{id}")
  public SaleResponse get(@PathVariable UUID id) {
    return responseAssembler.toSaleResponse(
        saleApplicationService.getById(new GetSaleByIdQuery(SaleId.of(id))));
  }

  @PostMapping("/sales/{id}/payments")
  public ResponseEntity<SaleResponse> recordPayment(
      @PathVariable UUID id, @Valid @RequestBody RecordPaymentRequest request) {
    SaleResponse response = responseAssembler.toSaleResponse(
        saleApplicationService.recordPayment(paymentAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at("/sales/{id}", response, id);
  }

  @PatchMapping("/sales/{id}/status")
  public SaleResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateSaleStatusRequest request) {
    SaleStatus status = parseSaleStatus(request.status());
    if (status != SaleStatus.CLOSED) {
      throw new DomainException("Sale status can only be changed to CLOSED through this operation.", 400);
    }
    return responseAssembler.toSaleResponse(
        saleApplicationService.close(new CloseSaleCommand(SaleId.of(id))));
  }

  private static SaleStatus parseSaleStatus(String status) {
    try {
      return SaleStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new DomainException("Sale status must be CLOSED.", 400);
    }
  }
}
