package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;

public record RateOpticalStoreCommand(
    PatientId patientId, OpticalStoreId opticalStoreId, StoreRating score, String comment) {
}
