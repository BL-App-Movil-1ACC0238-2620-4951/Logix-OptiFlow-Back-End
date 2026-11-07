package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import java.time.LocalDate;
import java.util.Optional;

public record RegisterPatientCommand(
    Name name,
    EmailAddress email,
    PhoneNumber phone,
    String password,
    Optional<LocalDate> birthDate) {}
