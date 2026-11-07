package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.LoginCommand;
import com.optiflow.platform.searchbooking.application.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.application.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PatientApplicationService {

  private final RegisterPatientCommandHandler registerPatientCommandHandler;
  private final LoginCommandHandler loginCommandHandler;
  private final GetPatientsByBirthdayQueryService getPatientsByBirthdayQueryService;

  public PatientApplicationService(
      RegisterPatientCommandHandler registerPatientCommandHandler,
      LoginCommandHandler loginCommandHandler,
      GetPatientsByBirthdayQueryService getPatientsByBirthdayQueryService) {
    this.registerPatientCommandHandler = registerPatientCommandHandler;
    this.loginCommandHandler = loginCommandHandler;
    this.getPatientsByBirthdayQueryService = getPatientsByBirthdayQueryService;
  }

  public Patient register(RegisterPatientCommand command) {
    return registerPatientCommandHandler.handle(command);
  }

  public LoginResult login(LoginCommand command) {
    return loginCommandHandler.handle(command);
  }

  public List<PatientId> findByBirthday(GetPatientsByBirthdayQuery query) {
    return getPatientsByBirthdayQueryService.handle(query);
  }
}
