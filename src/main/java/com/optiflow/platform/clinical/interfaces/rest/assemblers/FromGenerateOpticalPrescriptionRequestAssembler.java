package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.domain.model.commands.GenerateOpticalPrescriptionCommand;
import com.optiflow.platform.clinical.domain.model.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.model.valueobjects.OpticalPrescription;
import com.optiflow.platform.clinical.interfaces.rest.resources.GenerateOpticalPrescriptionRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromGenerateOpticalPrescriptionRequestAssembler {

  public GenerateOpticalPrescriptionCommand toCommand(
      UUID clinicalRecordId, GenerateOpticalPrescriptionRequest request) {
    return new GenerateOpticalPrescriptionCommand(
        ClinicalRecordId.of(clinicalRecordId),
        new OpticalPrescription(
            request.sphereOD(),
            request.sphereOS(),
            request.cylinderOD(),
            request.cylinderOS(),
            request.axisOD(),
            request.axisOS(),
            request.addition(),
            request.treatment(),
            request.recommendedFrameType()));
  }
}
