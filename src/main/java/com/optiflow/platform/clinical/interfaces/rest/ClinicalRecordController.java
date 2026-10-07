package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.application.queries.GetClinicalRecordsByPatientIdQuery;
import com.optiflow.platform.clinical.application.queries.GetOpticalPrescriptionQuery;
import com.optiflow.platform.clinical.application.services.ClinicalRecordApplicationService;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromGenerateOpticalPrescriptionRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRecordMedicalHistoryRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRegisterClinicalRecordRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.ClinicalRecordResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateOpticalPrescriptionRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.OpticalPrescriptionResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordMedicalHistoryRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterClinicalRecordRequest;
import com.optiflow.platform.shared.documentation.openapi.configuration.OpenApiTags;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.CLINICAL_RECORDS)
@RestController
public class ClinicalRecordController {

  private final ClinicalRecordApplicationService clinicalRecordApplicationService;
  private final FromRegisterClinicalRecordRequestAssembler registerAssembler;
  private final FromRecordMedicalHistoryRequestAssembler medicalHistoryAssembler;
  private final FromGenerateOpticalPrescriptionRequestAssembler prescriptionAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;

  public ClinicalRecordController(
      ClinicalRecordApplicationService clinicalRecordApplicationService,
      FromRegisterClinicalRecordRequestAssembler registerAssembler,
      FromRecordMedicalHistoryRequestAssembler medicalHistoryAssembler,
      FromGenerateOpticalPrescriptionRequestAssembler prescriptionAssembler,
      ClinicalCommercialResponseAssembler responseAssembler) {
    this.clinicalRecordApplicationService = clinicalRecordApplicationService;
    this.registerAssembler = registerAssembler;
    this.medicalHistoryAssembler = medicalHistoryAssembler;
    this.prescriptionAssembler = prescriptionAssembler;
    this.responseAssembler = responseAssembler;
  }

  @PostMapping("/clinical-records")
  @ResponseStatus(HttpStatus.CREATED)
  public ClinicalRecordResponse register(
      @Valid @RequestBody RegisterClinicalRecordRequest request) {
    return responseAssembler.toClinicalRecordResponse(
        clinicalRecordApplicationService.register(registerAssembler.toCommand(request)));
  }

  @GetMapping("/clinical-records/{id}")
  public ClinicalRecordResponse get(@PathVariable UUID id) {
    return responseAssembler.toClinicalRecordResponse(clinicalRecordApplicationService.getById(
        new GetClinicalRecordByIdQuery(ClinicalRecordId.of(id))));
  }

  @GetMapping("/patients/{patientId}/clinical-records")
  public List<ClinicalRecordResponse> findByPatient(@PathVariable UUID patientId) {
    return clinicalRecordApplicationService
        .findByPatient(new GetClinicalRecordsByPatientIdQuery(PatientId.of(patientId)))
        .stream()
        .map(responseAssembler::toClinicalRecordResponse)
        .toList();
  }

  @PutMapping("/clinical-records/{id}/medical-history")
  public ClinicalRecordResponse recordMedicalHistory(
      @PathVariable UUID id, @Valid @RequestBody RecordMedicalHistoryRequest request) {
    return responseAssembler.toClinicalRecordResponse(
        clinicalRecordApplicationService.recordMedicalHistory(
            medicalHistoryAssembler.toCommand(id, request)));
  }

  @PostMapping("/clinical-records/{id}/prescription")
  @ResponseStatus(HttpStatus.CREATED)
  public OpticalPrescriptionResponse generatePrescription(
      @PathVariable UUID id, @Valid @RequestBody GenerateOpticalPrescriptionRequest request) {
    return responseAssembler.toPrescriptionResponse(
        clinicalRecordApplicationService.generatePrescription(
            prescriptionAssembler.toCommand(id, request)));
  }

  @GetMapping("/clinical-records/{id}/prescription")
  public OpticalPrescriptionResponse getPrescription(@PathVariable UUID id) {
    return responseAssembler.toPrescriptionResponse(
        clinicalRecordApplicationService.getPrescription(
            new GetOpticalPrescriptionQuery(ClinicalRecordId.of(id))));
  }
}
