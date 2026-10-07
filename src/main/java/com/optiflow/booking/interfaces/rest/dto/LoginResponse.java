package com.optiflow.booking.interfaces.rest.dto;

public record LoginResponse(String token, PatientResponse patient) {
}
