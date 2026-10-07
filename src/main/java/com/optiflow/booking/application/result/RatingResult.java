package com.optiflow.booking.application.result;

import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.vo.StoreRating;

public record RatingResult(PatientStoreRating rating, StoreRating averageRating) {
}
