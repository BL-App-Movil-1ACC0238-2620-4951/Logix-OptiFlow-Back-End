package com.optiflow.booking.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterPatientRequest(
    @NotBlank @Size(min = 2, max = 80) String name,
    @NotBlank @Email String email,
    @NotBlank String phone,
    @NotBlank @Size(min = 8, max = 72) String password) {
}
