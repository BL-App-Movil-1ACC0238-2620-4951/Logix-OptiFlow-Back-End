package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import java.time.LocalDate;
import java.util.Optional;

public record UpdatePatientProfileCommand(
    PatientId patientId, Name name, PhoneNumber phone, Optional<LocalDate> birthDate) {}
