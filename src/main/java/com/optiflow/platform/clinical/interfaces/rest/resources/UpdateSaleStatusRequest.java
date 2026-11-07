package com.optiflow.platform.clinical.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateSaleStatusRequest(@NotBlank String status) {}
