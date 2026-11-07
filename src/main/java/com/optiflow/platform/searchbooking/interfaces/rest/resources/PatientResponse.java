package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
    UUID id, String name, String email, String phone, LocalDate birthDate, Instant createdAt) {}
