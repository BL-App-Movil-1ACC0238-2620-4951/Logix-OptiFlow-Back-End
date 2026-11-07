package com.optiflow.platform.searchbooking.application.queries;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import java.time.LocalDate;

public record GetAppointmentsByOpticalStoreQuery(OpticalStoreId opticalStoreId, LocalDate date) {}
