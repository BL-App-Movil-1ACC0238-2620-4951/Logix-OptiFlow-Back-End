package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.FavoriteStore;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.infrastructure.persistence.entity.FavoriteStoreEntity;
import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
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
