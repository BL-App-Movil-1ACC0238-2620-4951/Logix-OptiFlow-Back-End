package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.RecordMedicalHistoryCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.interfaces.rest.resources.RecordMedicalHistoryRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromRecordMedicalHistoryRequestAssembler {

  public RecordMedicalHistoryCommand toCommand(
      UUID clinicalRecordId, RecordMedicalHistoryRequest request) {
    return new RecordMedicalHistoryCommand(
        ClinicalRecordId.of(clinicalRecordId),
        request.allergies(),
        request.previousConditions(),
        request.familyOcularHistory());
  }
}
