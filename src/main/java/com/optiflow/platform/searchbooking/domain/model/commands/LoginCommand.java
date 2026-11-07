package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;

public record LoginCommand(EmailAddress email, String password) {
}
