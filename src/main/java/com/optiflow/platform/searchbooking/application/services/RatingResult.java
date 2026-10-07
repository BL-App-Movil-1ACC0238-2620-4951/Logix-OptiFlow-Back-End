package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;

public record RatingResult(PatientStoreRating rating, StoreRating averageRating) {
}
