package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.entities.Patient;

public record LoginResult(String token, Patient patient) {
}
