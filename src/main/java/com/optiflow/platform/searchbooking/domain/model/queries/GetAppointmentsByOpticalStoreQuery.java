package com.optiflow.platform.searchbooking.domain.model.queries;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import java.time.LocalDate;

public record GetAppointmentsByOpticalStoreQuery(OpticalStoreId opticalStoreId, LocalDate date) {}
