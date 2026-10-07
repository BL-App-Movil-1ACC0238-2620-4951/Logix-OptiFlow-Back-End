package com.optiflow.platform.searchbooking.interfaces.rest.resources;

public record LoginResponse(String token, PatientResponse patient) {
}
