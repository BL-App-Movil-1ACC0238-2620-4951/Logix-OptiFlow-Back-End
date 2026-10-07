package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.PhoneNumber;

public record RegisterPatientCommand(
    Name name, EmailAddress email, PhoneNumber phone, String password) {
}
