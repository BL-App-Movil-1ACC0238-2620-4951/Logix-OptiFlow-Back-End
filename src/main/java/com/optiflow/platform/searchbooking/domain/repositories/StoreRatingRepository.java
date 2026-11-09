package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface StoreRatingRepository {

  void save(PatientStoreRating rating);

  Optional<PatientStoreRating> find(PatientId patientId, OpticalStoreId opticalStoreId);

  List<PatientStoreRating> findByStore(OpticalStoreId opticalStoreId);
}
