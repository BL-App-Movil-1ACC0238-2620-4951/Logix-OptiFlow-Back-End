package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.command.LoginCommand;
import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.interfaces.rest.dto.LoginRequest;
import org.springframework.stereotype.Component;

@Component
public class FromLoginRequestAssembler {

  public LoginCommand toCommand(LoginRequest request) {
    return new LoginCommand(new EmailAddress(request.email()), request.password());
  }
}
