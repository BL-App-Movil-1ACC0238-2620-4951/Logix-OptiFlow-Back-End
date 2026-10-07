package com.optiflow.booking.domain.service;

import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.vo.StoreRating;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StoreRatingService {

  public void ensureNotRated(boolean alreadyRated) {
    if (alreadyRated) {
      throw new DomainException("This patient has already rated this optical store.", 409);
    }
  }

  public StoreRating average(List<PatientStoreRating> ratings) {
    if (ratings.isEmpty()) {
      return StoreRating.zero();
    }
    BigDecimal total = ratings.stream()
        .map(rating -> rating.score().value())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal mean = total.divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
    return StoreRating.of(mean);
  }
}
