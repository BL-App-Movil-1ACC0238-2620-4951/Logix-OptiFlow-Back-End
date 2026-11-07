package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreRating;

public record RateOpticalStoreCommand(
    PatientId patientId, OpticalStoreId opticalStoreId, StoreRating score, String comment) {
}
