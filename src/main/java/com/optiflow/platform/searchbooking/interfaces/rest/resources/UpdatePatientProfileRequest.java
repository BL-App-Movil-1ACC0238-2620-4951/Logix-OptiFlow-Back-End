package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePatientProfileRequest(
    @NotBlank @Size(min = 2, max = 80) String name,
    @NotBlank String phone,
    LocalDate birthDate) {}
