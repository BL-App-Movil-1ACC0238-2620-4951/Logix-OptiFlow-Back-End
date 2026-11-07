package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.domain.model.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.domain.model.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.application.services.QuotationApplicationService;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationStatus;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromApplyPromotionOrDiscountRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromGenerateQuotationRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.ApplyPromotionOrDiscountRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateQuotationRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.QuotationResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.UpdateQuotationStatusRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
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

@Tag(name = OpenApiTags.QUOTATIONS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
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
  public ResponseEntity<QuotationResponse> generate(@Valid @RequestBody GenerateQuotationRequest request) {
    QuotationResponse response = responseAssembler.toQuotationResponse(
        quotationApplicationService.generate(generateAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/quotations/{id}", response, response.id());
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

  @PatchMapping("/quotations/{id}/status")
  public QuotationResponse updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateQuotationStatusRequest request) {
    QuotationId quotationId = QuotationId.of(id);
    QuotationStatus status = parseQuotationStatus(request.status());
    return responseAssembler.toQuotationResponse(switch (status) {
      case APPROVED -> quotationApplicationService.approve(new ApproveQuotationCommand(quotationId));
      case REJECTED -> {
        String reason = request.reason();
        if (reason == null || reason.isBlank()) {
          throw new DomainException("A rejection reason is required.", 400);
        }
        yield quotationApplicationService.reject(new RejectQuotationCommand(quotationId, reason));
      }
      case DRAFT -> throw new DomainException("Quotation status cannot be changed to DRAFT.", 400);
    });
  }

  private static QuotationStatus parseQuotationStatus(String status) {
    try {
      return QuotationStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new DomainException("Quotation status must be APPROVED or REJECTED.", 400);
    }
  }
}
