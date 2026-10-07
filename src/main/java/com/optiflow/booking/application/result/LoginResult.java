package com.optiflow.booking.application.result;

import com.optiflow.booking.domain.model.Patient;

public record LoginResult(String token, Patient patient) {
}
