package com.optiflow.booking.application.command;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;

public record SaveFavoriteOpticalStoreCommand(PatientId patientId, OpticalStoreId opticalStoreId) {
}
