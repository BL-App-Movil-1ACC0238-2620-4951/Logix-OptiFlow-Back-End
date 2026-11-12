package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.domain.model.commands.UpdatePatientProfileCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.Patient;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Updates the name, phone and birth date of a patient. */
@Service
public class UpdatePatientProfileCommandHandler {

  private final PatientRepository patientRepository;

  public UpdatePatientProfileCommandHandler(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  @Transactional
  public Patient handle(UpdatePatientProfileCommand command) {
    Patient patient = patientRepository.findById(command.patientId())
        .orElseThrow(() -> new DomainException("The patient was not found.", 404));
    patient.updateProfile(command.name(), command.phone(), command.birthDate().orElse(null));
    patientRepository.update(patient);
    return patient;
  }
}
