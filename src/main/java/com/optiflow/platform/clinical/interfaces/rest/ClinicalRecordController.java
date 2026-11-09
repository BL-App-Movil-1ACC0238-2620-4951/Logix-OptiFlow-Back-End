package com.optiflow.platform.clinical.interfaces.rest;

import com.optiflow.platform.clinical.application.services.ClinicalRecordApplicationService;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordsByPatientIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordsSummaryQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetOpticalPrescriptionQuery;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.PatientId;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.ClinicalCommercialResponseAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromGenerateOpticalPrescriptionRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRecordMedicalHistoryRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.assemblers.FromRegisterClinicalRecordRequestAssembler;
import com.optiflow.platform.clinical.interfaces.rest.resources.ClinicalRecordResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.ClinicalRecordsSummaryResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateOpticalPrescriptionRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.OpticalPrescriptionResponse;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordMedicalHistoryRequest;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterClinicalRecordRequest;
import com.optiflow.platform.shared.infrastructure.documentation.openapi.configuration.OpenApiTags;
import com.optiflow.platform.shared.infrastructure.security.AuthenticatedUser;
import com.optiflow.platform.shared.infrastructure.security.PatientDataAccessGuard;
import com.optiflow.platform.shared.interfaces.rest.ApiV1;
import com.optiflow.platform.shared.interfaces.rest.CreatedResourceResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiTags.CLINICAL_RECORDS)
@RestController
@RequestMapping(ApiV1.BASE_PATH)
public class ClinicalRecordController {

  private final ClinicalRecordApplicationService clinicalRecordApplicationService;
  private final FromRegisterClinicalRecordRequestAssembler registerAssembler;
  private final FromRecordMedicalHistoryRequestAssembler medicalHistoryAssembler;
  private final FromGenerateOpticalPrescriptionRequestAssembler prescriptionAssembler;
  private final ClinicalCommercialResponseAssembler responseAssembler;
  private final PatientDataAccessGuard patientDataAccessGuard;

  public ClinicalRecordController(
      ClinicalRecordApplicationService clinicalRecordApplicationService,
      FromRegisterClinicalRecordRequestAssembler registerAssembler,
      FromRecordMedicalHistoryRequestAssembler medicalHistoryAssembler,
      FromGenerateOpticalPrescriptionRequestAssembler prescriptionAssembler,
      ClinicalCommercialResponseAssembler responseAssembler,
      PatientDataAccessGuard patientDataAccessGuard) {
    this.clinicalRecordApplicationService = clinicalRecordApplicationService;
    this.registerAssembler = registerAssembler;
    this.medicalHistoryAssembler = medicalHistoryAssembler;
    this.prescriptionAssembler = prescriptionAssembler;
    this.responseAssembler = responseAssembler;
    this.patientDataAccessGuard = patientDataAccessGuard;
  }

  @Operation(summary = "Register a clinical record",
      description = "The optometrist registers the examination of the booked appointment; the "
          + "record opened by AppointmentBooked becomes OPEN.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Clinical record registered."),
      @ApiResponse(responseCode = "404", description = "The appointment was not found."),
      @ApiResponse(responseCode = "409",
          description = "The appointment is cancelled, belongs to another patient or is "
              + "already registered.")
  })
  @PostMapping("/clinical-records")
  public ResponseEntity<ClinicalRecordResponse> register(
      @Valid @RequestBody RegisterClinicalRecordRequest request) {
    ClinicalRecordResponse response = responseAssembler.toClinicalRecordResponse(
        clinicalRecordApplicationService.register(registerAssembler.toCommand(request)));
    return CreatedResourceResponses.at("/clinical-records/{id}", response, response.id());
  }

  @Operation(summary = "Get the clinical records summary",
      description = "Returns how many patients have at least one clinical record, for the staff "
          + "directory.")
  @ApiResponses(@ApiResponse(responseCode = "200",
      description = "Summary of the clinical records."))
  @GetMapping("/clinical-records/summary")
  public ClinicalRecordsSummaryResponse summary() {
    return responseAssembler.toClinicalRecordsSummaryResponse(
        clinicalRecordApplicationService.getSummary(new GetClinicalRecordsSummaryQuery()));
  }

  @Operation(summary = "Get a clinical record",
      description = "Returns the record with its medical history and prescription. A patient can "
          + "only read their own records.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Clinical record found."),
      @ApiResponse(responseCode = "403", description = "The record belongs to another patient."),
      @ApiResponse(responseCode = "404", description = "The clinical record was not found.")
  })
  @GetMapping("/clinical-records/{id}")
  public ClinicalRecordResponse get(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    ClinicalRecord record = clinicalRecordApplicationService.getById(
        new GetClinicalRecordByIdQuery(ClinicalRecordId.of(id)));
    patientDataAccessGuard.ensureSelfPatientAccess(user, record.patientId().value());
    return responseAssembler.toClinicalRecordResponse(record);
  }

  @Operation(summary = "List the clinical records of a patient",
      description = "Returns the clinical history of the patient. A patient can only read "
          + "their own records.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Clinical records of the patient."),
      @ApiResponse(responseCode = "403",
          description = "A patient asked for another patient's records.")
  })
  @GetMapping("/patients/{patientId}/clinical-records")
  public List<ClinicalRecordResponse> findByPatient(
      @PathVariable UUID patientId, @AuthenticationPrincipal AuthenticatedUser user) {
    patientDataAccessGuard.ensureSelfPatientAccess(user, patientId);
    return clinicalRecordApplicationService
        .findByPatient(new GetClinicalRecordsByPatientIdQuery(PatientId.of(patientId)))
        .stream()
        .map(responseAssembler::toClinicalRecordResponse)
        .toList();
  }

  @Operation(summary = "Record the medical history",
      description = "Replaces the allergies, previous conditions and family ocular history of the "
          + "record.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Medical history recorded."),
      @ApiResponse(responseCode = "404", description = "The clinical record was not found."),
      @ApiResponse(responseCode = "409", description = "The clinical record is closed.")
  })
  @PutMapping("/clinical-records/{id}/medical-history")
  public ClinicalRecordResponse recordMedicalHistory(
      @PathVariable UUID id, @Valid @RequestBody RecordMedicalHistoryRequest request) {
    return responseAssembler.toClinicalRecordResponse(
        clinicalRecordApplicationService.recordMedicalHistory(
            medicalHistoryAssembler.toCommand(id, request)));
  }

  @Operation(summary = "Generate the optical prescription",
      description = "Registers the sphere, cylinder and axis of each eye of an examined patient.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Prescription generated."),
      @ApiResponse(responseCode = "404", description = "The clinical record was not found."),
      @ApiResponse(responseCode = "409",
          description = "The patient was not examined yet or the record is closed.")
  })
  @PostMapping("/clinical-records/{id}/prescription")
  public ResponseEntity<OpticalPrescriptionResponse> generatePrescription(
      @PathVariable UUID id, @Valid @RequestBody GenerateOpticalPrescriptionRequest request) {
    OpticalPrescriptionResponse response = responseAssembler.toPrescriptionResponse(
        clinicalRecordApplicationService.generatePrescription(
            prescriptionAssembler.toCommand(id, request)));
    return CreatedResourceResponses.at(
        "/clinical-records/{id}/prescription", response, id);
  }

  @Operation(summary = "Get the optical prescription",
      description = "Returns the prescription of the record. A patient can only read their own "
          + "prescriptions.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Prescription found."),
      @ApiResponse(responseCode = "403", description = "The record belongs to another patient."),
      @ApiResponse(responseCode = "404",
          description = "The clinical record or its prescription was not found.")
  })
  @GetMapping("/clinical-records/{id}/prescription")
  public OpticalPrescriptionResponse getPrescription(
      @PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
    ClinicalRecord record = clinicalRecordApplicationService.getPrescription(
        new GetOpticalPrescriptionQuery(ClinicalRecordId.of(id)));
    patientDataAccessGuard.ensureSelfPatientAccess(user, record.patientId().value());
    return responseAssembler.toPrescriptionResponse(record);
  }
}
