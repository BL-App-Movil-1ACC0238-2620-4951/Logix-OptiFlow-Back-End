package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.services.SaleApplicationService;
import com.optiflow.platform.clinical.domain.model.commands.CloseSaleCommand;
import com.optiflow.platform.clinical.domain.model.commands.CreatePaymentPreferenceCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetSaleByIdQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleId;
import com.optiflow.platform.clinical.domain.model.valueobjects.SaleStatus;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRecordPaymentRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRegisterSaleRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.PaymentPreferenceResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordPaymentRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterSaleRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.SaleResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.UpdateSaleStatusRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final FromRegisterSaleRequestAssembler registerAssembler;
  private final FromRecordPaymentRequestAssembler paymentAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;

  public SaleController(
      SaleApplicationService saleApplicationService,
      FromRegisterSaleRequestAssembler registerAssembler,
      FromRecordPaymentRequestAssembler paymentAssembler,
      ClinicalCommercialResponseAssembler responseAssembler) {
    this.saleApplicationService = saleApplicationService;
    this.registerAssembler = registerAssembler;
    this.paymentAssembler = paymentAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/sales")
  public ResponseEntity<SaleResponse> register(@Valid @RequestBody RegisterSaleRequest request) {
    SaleResponse response = responseAssembler.toSaleResponse(
        saleApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/sales/{id}", response, response.id());
  }

  @GetMapping("/sales/{id}")
  public SaleResponse get(@PathVariable UUID id) {
    return responseAssembler.toSaleResponse(
        saleApplicationService.getById(new GetSaleByIdQuery(SaleId.of(id))));
  }

  @Operation(summary = "Create the payment preference of a sale",
      description = "The patient asks to pay a sale pending payment from the app. Returns the "
          + "Checkout Pro link (initPoint) where the patient pays with card or Yape; the result "
          + "arrives later through the payment notifications. Only the patient of the sale.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Payment preference created."),
      @ApiResponse(responseCode = "403", description = "The sale belongs to another patient."),
      @ApiResponse(responseCode = "404", description = "The sale was not found."),
      @ApiResponse(responseCode = "409", description = "The sale is not pending payment."),
      @ApiResponse(responseCode = "503", description = "The payment gateway is not available.")
  })
  @PostMapping("/sales/{id}/payment-preference")
  public ResponseEntity<PaymentPreferenceResponse> createPaymentPreference(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    PaymentPreferenceResponse response = responseAssembler.toPaymentPreferenceResponse(
        saleApplicationService.createPaymentPreference(new CreatePaymentPreferenceCommand(
            SaleId.of(id), user == null ? null : PatientId.of(user.userId()))));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
