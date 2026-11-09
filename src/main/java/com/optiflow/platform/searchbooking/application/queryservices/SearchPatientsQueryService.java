package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.queries.SearchPatientsQuery;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchPatientsQueryService {

  private final PatientRepository patientRepository;

  public SearchPatientsQueryService(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  @Transactional(readOnly = true)
  public List<Patient> handle(SearchPatientsQuery query) {
    return patientRepository.search(query.name(), query.email());
  }
}
