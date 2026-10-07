package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.application.commands.RegisterSaleCommand;
import com.optiflow.platform.clinical.application.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.application.services.SaleApplicationService;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.SaleId;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRecordPaymentRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordPaymentRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterSaleRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.SaleResponse;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.SALES)
@RestController
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
  @ResponseStatus(HttpStatus.CREATED)
  public SaleResponse register(@Valid @RequestBody RegisterSaleRequest request) {
    return responseAssembler.toSaleResponse(saleApplicationService.register(
        new RegisterSaleCommand(QuotationId.of(request.quotationId()))));
  }

  @GetMapping("/sales/{id}")
  public SaleResponse get(@PathVariable UUID id) {
    return responseAssembler.toSaleResponse(
        saleApplicationService.getById(new GetSaleByIdQuery(SaleId.of(id))));
  }

  @PostMapping("/sales/{id}/payments")
  @ResponseStatus(HttpStatus.CREATED)
  public SaleResponse recordPayment(
      @PathVariable UUID id, @Valid @RequestBody RecordPaymentRequest request) {
    return responseAssembler.toSaleResponse(
        saleApplicationService.recordPayment(paymentAssembler.toCommand(id, request)));
  }

  @PatchMapping("/sales/{id}/close")
  public SaleResponse close(@PathVariable UUID id) {
    return responseAssembler.toSaleResponse(
        saleApplicationService.close(new CloseSaleCommand(SaleId.of(id))));
  }
}
