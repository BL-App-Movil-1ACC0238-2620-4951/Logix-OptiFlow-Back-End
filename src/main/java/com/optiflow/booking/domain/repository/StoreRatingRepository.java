package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import java.util.List;
import java.util.Optional;

public interface StoreRatingRepository {

  void save(PatientStoreRating rating);

  Optional<PatientStoreRating> find(PatientId patientId, OpticalStoreId opticalStoreId);

  List<PatientStoreRating> findByStore(OpticalStoreId opticalStoreId);
}
