package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.application.commands.GenerateOpticalPrescriptionCommand;
import com.optiflow.platform.clinical.application.commands.RecordMedicalHistoryCommand;
import com.optiflow.platform.clinical.application.commands.RegisterClinicalRecordCommand;
import com.optiflow.platform.clinical.application.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.application.queries.GetClinicalRecordsByPatientIdQuery;
import com.optiflow.platform.clinical.application.queries.GetOpticalPrescriptionQuery;
import com.optiflow.platform.clinical.domain.entities.ClinicalRecord;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClinicalRecordApplicationService {

  private final RegisterClinicalRecordHandler registerClinicalRecordHandler;
  private final RecordMedicalHistoryHandler recordMedicalHistoryHandler;
  private final GenerateOpticalPrescriptionHandler generateOpticalPrescriptionHandler;
  private final ClinicalRecordQueryService clinicalRecordQueryService;

  public ClinicalRecordApplicationService(
      RegisterClinicalRecordHandler registerClinicalRecordHandler,
      RecordMedicalHistoryHandler recordMedicalHistoryHandler,
      GenerateOpticalPrescriptionHandler generateOpticalPrescriptionHandler,
      ClinicalRecordQueryService clinicalRecordQueryService) {
    this.registerClinicalRecordHandler = registerClinicalRecordHandler;
    this.recordMedicalHistoryHandler = recordMedicalHistoryHandler;
    this.generateOpticalPrescriptionHandler = generateOpticalPrescriptionHandler;
    this.clinicalRecordQueryService = clinicalRecordQueryService;
  }

  public ClinicalRecord register(RegisterClinicalRecordCommand command) {
    return registerClinicalRecordHandler.handle(command);
  }

  public ClinicalRecord recordMedicalHistory(RecordMedicalHistoryCommand command) {
    return recordMedicalHistoryHandler.handle(command);
  }

  public ClinicalRecord generatePrescription(GenerateOpticalPrescriptionCommand command) {
    return generateOpticalPrescriptionHandler.handle(command);
  }

  public ClinicalRecord getById(GetClinicalRecordByIdQuery query) {
    return clinicalRecordQueryService.handle(query);
  }

  public List<ClinicalRecord> findByPatient(GetClinicalRecordsByPatientIdQuery query) {
    return clinicalRecordQueryService.handle(query);
  }

  public ClinicalRecord getPrescription(GetOpticalPrescriptionQuery query) {
    return clinicalRecordQueryService.handle(query);
  }
}
