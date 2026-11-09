package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterPatientRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterPatientRequestAssembler {

  public RegisterPatientCommand toCommand(RegisterPatientRequest request) {
    return new RegisterPatientCommand(
        new Name(request.name()),
        new EmailAddress(request.email()),
        new PhoneNumber(request.phone()),
        request.password(),
        Optional.ofNullable(request.birthDate()));
  }
}
