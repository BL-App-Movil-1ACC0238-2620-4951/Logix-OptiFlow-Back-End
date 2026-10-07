package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.FavoriteStore;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import java.util.Optional;

public interface FavoriteStoreRepository {

  void save(FavoriteStore favoriteStore);

  Optional<FavoriteStore> find(PatientId patientId, OpticalStoreId opticalStoreId);
}
