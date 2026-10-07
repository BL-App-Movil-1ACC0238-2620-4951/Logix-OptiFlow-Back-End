package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface StoreRatingRepository {

  void save(PatientStoreRating rating);

  Optional<PatientStoreRating> find(PatientId patientId, OpticalStoreId opticalStoreId);

  List<PatientStoreRating> findByStore(OpticalStoreId opticalStoreId);
}
