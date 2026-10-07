package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.FavoriteStoreEntity;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import org.springframework.stereotype.Component;

@Component
public class FavoriteStoreMapper {

  public FavoriteStore toDomain(FavoriteStoreEntity entity) {
    return FavoriteStore.reconstitute(
        PatientId.of(entity.getId().getPatientId()),
        OpticalStoreId.of(entity.getId().getOpticalStoreId()),
        entity.getSavedAt());
  }

  public FavoriteStoreEntity toEntity(FavoriteStore favoriteStore) {
    FavoriteStoreEntity entity = new FavoriteStoreEntity();
    entity.setId(new PatientStoreKey(
        favoriteStore.patientId().value(), favoriteStore.opticalStoreId().value()));
    entity.setSavedAt(favoriteStore.savedAt());
    return entity;
  }
}
