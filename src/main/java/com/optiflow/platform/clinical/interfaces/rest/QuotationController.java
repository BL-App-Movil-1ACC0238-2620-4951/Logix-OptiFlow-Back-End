package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.services.ClinicalRecordApplicationService;
import com.optiflow.platform.clinical.application.services.QuotationApplicationService;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.aggregates.Quotation;
import com.optiflow.platform.clinical.domain.model.commands.ApproveQuotationCommand;
import com.optiflow.platform.clinical.domain.model.commands.RejectQuotationCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetQuotationByIdQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.QuotationId;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromApplyPromotionOrDiscountRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromGenerateQuotationRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromUpdateQuotationStatusRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.ApplyPromotionOrDiscountRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateQuotationRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.QuotationResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.UpdateQuotationStatusRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final FromUpdateQuotationStatusRequestAssembler statusAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;
  private final ClinicalRecordApplicationService clinicalRecordApplicationService;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public QuotationController(
      QuotationApplicationService quotationApplicationService,
      FromGenerateQuotationRequestAssembler generateAssembler,
      FromApplyPromotionOrDiscountRequestAssembler discountAssembler,
      FromUpdateQuotationStatusRequestAssembler statusAssembler,
      ClinicalCommercialResponseAssembler responseAssembler,
      ClinicalRecordApplicationService clinicalRecordApplicationService,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.quotationApplicationService = quotationApplicationService;
    this.generateAssembler = generateAssembler;
    this.discountAssembler = discountAssembler;
    this.statusAssembler = statusAssembler;
    this.responseAssembler = responseAssembler;
    this.clinicalRecordApplicationService = clinicalRecordApplicationService;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @PostMapping("/quotations")
  public ResponseEntity<QuotationResponse> generate(@Valid @RequestBody GenerateQuotationRequest request) {
    QuotationResponse response = responseAssembler.toQuotationResponse(
        quotationApplicationService.generate(generateAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/quotations/{id}", response, response.id());
  }

  @GetMapping("/quotations/{id}")
  public QuotationResponse get(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    Quotation quotation =
        quotationApplicationService.getById(new GetQuotationByIdQuery(QuotationId.of(id)));
    if (user != null && "PATIENT".equals(user.role())) {
      // A quotation belongs to the patient of its clinical record.
      ClinicalRecord record = clinicalRecordApplicationService.getById(
          new GetClinicalRecordByIdQuery(quotation.clinicalRecordId()));
      patientDataAccessGuard.ensureSelfPatientAccess(user, record.patientId().value());
    }
    return responseAssembler.toQuotationResponse(quotation);
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
    return responseAssembler.toQuotationResponse(
        switch (statusAssembler.toCommand(id, request)) {
          case ApproveQuotationCommand command -> quotationApplicationService.approve(command);
          case RejectQuotationCommand command -> quotationApplicationService.reject(command);
        });
  }
}
