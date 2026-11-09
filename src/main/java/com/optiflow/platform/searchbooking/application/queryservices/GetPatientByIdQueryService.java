package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.model.queries.GetPatientByIdQuery;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetPatientByIdQueryService {

  private final PatientRepository patientRepository;

  public GetPatientByIdQueryService(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  @Transactional(readOnly = true)
  public Patient handle(GetPatientByIdQuery query) {
    return patientRepository.findById(query.patientId())
        .orElseThrow(() -> new DomainException("Patient was not found.", 404));
  }
}
