package com.optiflow.platform.searchbooking.application.results;

import com.optiflow.platform.searchbooking.domain.model.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreRating;

public record RatingResult(PatientStoreRating rating, StoreRating averageRating) {
}
