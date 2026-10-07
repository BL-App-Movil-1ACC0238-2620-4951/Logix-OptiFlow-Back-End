package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.command.RegisterPatientCommand;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.Name;
import com.optiflow.booking.domain.vo.PhoneNumber;
import com.optiflow.booking.interfaces.rest.dto.RegisterPatientRequest;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterPatientRequestAssembler {

  public RegisterPatientCommand toCommand(RegisterPatientRequest request) {
    return new RegisterPatientCommand(
        new Name(request.name()),
        new EmailAddress(request.email()),
        new PhoneNumber(request.phone()),
        request.password());
  }
}
