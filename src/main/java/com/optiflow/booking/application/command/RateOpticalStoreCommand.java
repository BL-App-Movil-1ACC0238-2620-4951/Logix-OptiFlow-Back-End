package com.optiflow.booking.application.command;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.StoreRating;

public record RateOpticalStoreCommand(
    PatientId patientId, OpticalStoreId opticalStoreId, StoreRating score, String comment) {
}
