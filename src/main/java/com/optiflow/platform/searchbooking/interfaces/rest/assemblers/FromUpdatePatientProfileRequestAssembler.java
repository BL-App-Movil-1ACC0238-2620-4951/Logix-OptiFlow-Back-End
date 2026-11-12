package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.UpdatePatientProfileCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.UpdatePatientProfileRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromUpdatePatientProfileRequestAssembler {

  public UpdatePatientProfileCommand toCommand(UUID patientId, UpdatePatientProfileRequest request) {
    return new UpdatePatientProfileCommand(
        PatientId.of(patientId),
        new Name(request.name()),
        new PhoneNumber(request.phone()),
        Optional.ofNullable(request.birthDate()));
  }
}
