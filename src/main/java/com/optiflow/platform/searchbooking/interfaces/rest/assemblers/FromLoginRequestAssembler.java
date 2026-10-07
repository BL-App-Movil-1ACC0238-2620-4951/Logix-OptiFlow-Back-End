package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.commands.LoginCommand;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginRequest;
import org.springframework.stereotype.Component;

@Component
public class FromLoginRequestAssembler {

  public LoginCommand toCommand(LoginRequest request) {
    return new LoginCommand(new EmailAddress(request.email()), request.password());
  }
}
