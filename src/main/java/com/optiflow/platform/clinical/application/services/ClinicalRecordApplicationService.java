package com.optiflow.platform.clinical.application.services;

import com.optiflow.platform.clinical.domain.model.commands.GenerateOpticalPrescriptionCommand;
import com.optiflow.platform.clinical.domain.model.commands.RecordMedicalHistoryCommand;
import com.optiflow.platform.clinical.domain.model.commands.RegisterClinicalRecordCommand;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordByIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetClinicalRecordsByPatientIdQuery;
import com.optiflow.platform.clinical.domain.model.queries.GetOpticalPrescriptionQuery;
import com.optiflow.platform.clinical.domain.model.aggregates.ClinicalRecord;
import java.util.List;
import org.springframework.stereotype.Service;
import com.optiflow.platform.clinical.application.commandhandlers.GenerateOpticalPrescriptionHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RecordMedicalHistoryHandler;
import com.optiflow.platform.clinical.application.commandhandlers.RegisterClinicalRecordHandler;
import com.optiflow.platform.clinical.application.queryservices.ClinicalRecordQueryService;

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
