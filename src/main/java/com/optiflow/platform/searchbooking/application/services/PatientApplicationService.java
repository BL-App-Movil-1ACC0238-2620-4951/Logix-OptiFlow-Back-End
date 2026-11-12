package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commandhandlers.LoginCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.RegisterPatientCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.UpdatePatientProfileCommandHandler;
import com.optiflow.platform.searchbooking.application.queryservices.GetPatientByIdQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.GetPatientsByBirthdayQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.SearchPatientsQueryService;
import com.optiflow.platform.searchbooking.application.results.LoginResult;
import com.optiflow.platform.searchbooking.domain.model.commands.LoginCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RegisterPatientCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.UpdatePatientProfileCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientByIdQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.SearchPatientsQuery;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PatientApplicationService {

  private final RegisterPatientCommandHandler registerPatientCommandHandler;
  private final UpdatePatientProfileCommandHandler updatePatientProfileCommandHandler;
  private final LoginCommandHandler loginCommandHandler;
  private final GetPatientsByBirthdayQueryService getPatientsByBirthdayQueryService;
  private final GetPatientByIdQueryService getPatientByIdQueryService;
  private final SearchPatientsQueryService searchPatientsQueryService;

  public PatientApplicationService(
      RegisterPatientCommandHandler registerPatientCommandHandler,
      UpdatePatientProfileCommandHandler updatePatientProfileCommandHandler,
      LoginCommandHandler loginCommandHandler,
      GetPatientsByBirthdayQueryService getPatientsByBirthdayQueryService,
      GetPatientByIdQueryService getPatientByIdQueryService,
      SearchPatientsQueryService searchPatientsQueryService) {
    this.registerPatientCommandHandler = registerPatientCommandHandler;
    this.updatePatientProfileCommandHandler = updatePatientProfileCommandHandler;
    this.loginCommandHandler = loginCommandHandler;
    this.getPatientsByBirthdayQueryService = getPatientsByBirthdayQueryService;
    this.getPatientByIdQueryService = getPatientByIdQueryService;
    this.searchPatientsQueryService = searchPatientsQueryService;
  }

  public Patient register(RegisterPatientCommand command) {
    return registerPatientCommandHandler.handle(command);
  }

  public Patient updateProfile(UpdatePatientProfileCommand command) {
    return updatePatientProfileCommandHandler.handle(command);
  }

  public LoginResult login(LoginCommand command) {
    return loginCommandHandler.handle(command);
  }

  public List<PatientId> findByBirthday(GetPatientsByBirthdayQuery query) {
    return getPatientsByBirthdayQueryService.handle(query);
  }

  public Patient get(GetPatientByIdQuery query) {
    return getPatientByIdQueryService.handle(query);
  }

  public List<Patient> search(SearchPatientsQuery query) {
    return searchPatientsQueryService.handle(query);
  }
}
