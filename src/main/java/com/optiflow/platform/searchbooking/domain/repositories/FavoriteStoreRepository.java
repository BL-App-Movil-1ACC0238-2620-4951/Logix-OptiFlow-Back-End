package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import java.util.Optional;

public interface FavoriteStoreRepository {

  void save(FavoriteStore favoriteStore);

  Optional<FavoriteStore> find(PatientId patientId, OpticalStoreId opticalStoreId);
}
