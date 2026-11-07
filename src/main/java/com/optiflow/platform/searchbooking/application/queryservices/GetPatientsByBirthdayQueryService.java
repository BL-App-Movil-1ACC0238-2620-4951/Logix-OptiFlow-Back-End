package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientsByBirthdayQuery;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPatientsByBirthdayQueryService {

  private final PatientRepository patientRepository;

  public GetPatientsByBirthdayQueryService(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  @Transactional(readOnly = true)
  public List<PatientId> handle(GetPatientsByBirthdayQuery query) {
    return patientRepository.findIdsByBirthday(query.date());
  }
}
