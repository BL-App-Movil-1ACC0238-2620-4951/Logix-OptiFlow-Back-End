package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commandhandlers.LoginCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.RegisterPatientCommandHandler;
import com.optiflow.platform.searchbooking.application.queryservices.GetPatientsByBirthdayQueryService;
import com.optiflow.platform.searchbooking.application.results.LoginResult;
import com.optiflow.platform.searchbooking.domain.model.commands.LoginCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
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
