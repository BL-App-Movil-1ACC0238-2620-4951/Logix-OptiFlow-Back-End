package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;

public record LoginCommand(EmailAddress email, String password) {
}
