package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;

public record SaveFavoriteOpticalStoreCommand(PatientId patientId, OpticalStoreId opticalStoreId) {
}
