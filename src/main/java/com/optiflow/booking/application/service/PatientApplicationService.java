package com.optiflow.booking.application.service;

import com.optiflow.booking.application.command.LoginCommand;
import com.optiflow.booking.application.command.RegisterPatientCommand;
import com.optiflow.booking.application.handler.LoginCommandHandler;
import com.optiflow.booking.application.handler.RegisterPatientCommandHandler;
import com.optiflow.booking.application.result.LoginResult;
import com.optiflow.booking.domain.model.Patient;
import org.springframework.stereotype.Service;

@Service
public class PatientApplicationService {

  private final RegisterPatientCommandHandler registerPatientCommandHandler;
  private final LoginCommandHandler loginCommandHandler;

  public PatientApplicationService(
      RegisterPatientCommandHandler registerPatientCommandHandler,
      LoginCommandHandler loginCommandHandler) {
    this.registerPatientCommandHandler = registerPatientCommandHandler;
    this.loginCommandHandler = loginCommandHandler;
  }

  public Patient register(RegisterPatientCommand command) {
    return registerPatientCommandHandler.handle(command);
  }

  public LoginResult login(LoginCommand command) {
    return loginCommandHandler.handle(command);
  }
}
