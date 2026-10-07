package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.LoginCommand;
import com.optiflow.platform.searchbooking.application.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.entities.Patient;
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
