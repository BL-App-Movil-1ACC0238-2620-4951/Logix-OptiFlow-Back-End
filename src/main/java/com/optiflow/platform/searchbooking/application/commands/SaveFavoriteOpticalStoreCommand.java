package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;

public record SaveFavoriteOpticalStoreCommand(PatientId patientId, OpticalStoreId opticalStoreId) {
}
