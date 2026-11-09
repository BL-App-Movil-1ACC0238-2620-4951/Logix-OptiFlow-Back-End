package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import java.util.Optional;

public interface FavoriteStoreRepository {

  void save(FavoriteStore favoriteStore);

  Optional<FavoriteStore> find(PatientId patientId, OpticalStoreId opticalStoreId);
}
