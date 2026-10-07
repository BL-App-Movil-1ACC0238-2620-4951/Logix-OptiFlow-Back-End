package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.application.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.application.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.application.services.QuotationApplicationService;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromApplyPromotionOrDiscountRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromGenerateQuotationRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.ApplyPromotionOrDiscountRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateQuotationRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.QuotationResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.RejectQuotationRequest;
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

@RestController
public class QuotationController {

  private final QuotationApplicationService quotationApplicationService;
  private final FromGenerateQuotationRequestAssembler generateAssembler;
  private final FromApplyPromotionOrDiscountRequestAssembler discountAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;

  public QuotationController(
      QuotationApplicationService quotationApplicationService,
      FromGenerateQuotationRequestAssembler generateAssembler,
      FromApplyPromotionOrDiscountRequestAssembler discountAssembler,
      ClinicalCommercialResponseAssembler responseAssembler) {
    this.quotationApplicationService = quotationApplicationService;
    this.generateAssembler = generateAssembler;
    this.discountAssembler = discountAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/quotations")
  @ResponseStatus(HttpStatus.CREATED)
  public QuotationResponse generate(@Valid @RequestBody GenerateQuotationRequest request) {
    return responseAssembler.toQuotationResponse(
        quotationApplicationService.generate(generateAssembler.toCommand(request)));
  }

  @GetMapping("/quotations/{id}")
  public QuotationResponse get(@PathVariable UUID id) {
    return responseAssembler.toQuotationResponse(
        quotationApplicationService.getById(new GetQuotationByIdQuery(QuotationId.of(id))));
  }

  @PatchMapping("/quotations/{id}/discount")
  public QuotationResponse applyPromotionOrDiscount(
      @PathVariable UUID id, @Valid @RequestBody ApplyPromotionOrDiscountRequest request) {
    return responseAssembler.toQuotationResponse(
        quotationApplicationService.applyPromotionOrDiscount(
            discountAssembler.toCommand(id, request)));
  }

  @PatchMapping("/quotations/{id}/approve")
  public QuotationResponse approve(@PathVariable UUID id) {
    return responseAssembler.toQuotationResponse(
        quotationApplicationService.approve(new ApproveQuotationCommand(QuotationId.of(id))));
  }

  @PatchMapping("/quotations/{id}/reject")
  public QuotationResponse reject(
      @PathVariable UUID id, @Valid @RequestBody RejectQuotationRequest request) {
    return responseAssembler.toQuotationResponse(quotationApplicationService.reject(
        new RejectQuotationCommand(QuotationId.of(id), request.reason())));
  }
}
