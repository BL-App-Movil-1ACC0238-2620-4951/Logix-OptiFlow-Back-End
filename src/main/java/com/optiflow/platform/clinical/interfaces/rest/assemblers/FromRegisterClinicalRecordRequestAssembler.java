package com.optiflow.platform.clinical.interfaces.rest.assemblers;

import com.optiflow.platform.clinical.application.commands.RegisterClinicalRecordCommand;
import com.optiflow.platform.clinical.domain.valueobjects.AppointmentId;
import com.optiflow.platform.clinical.domain.valueobjects.PatientId;
import com.optiflow.platform.clinical.interfaces.rest.resources.RegisterClinicalRecordRequest;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterClinicalRecordRequestAssembler {

  public RegisterClinicalRecordCommand toCommand(RegisterClinicalRecordRequest request) {
    return new RegisterClinicalRecordCommand(
        PatientId.of(request.patientId()),
        AppointmentId.of(request.appointmentId()),
        request.examinationDate(),
        request.observations());
  }
}
