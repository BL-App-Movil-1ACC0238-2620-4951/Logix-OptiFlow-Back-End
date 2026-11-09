package com.optiflow.platform.searchbooking.domain.model.queries;

import java.time.LocalDate;

public record GetPatientsByBirthdayQuery(LocalDate date) {}
