package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.StoreRatingEntity;
import org.springframework.stereotype.Component;

@Component
public class StoreRatingMapper {

  public PatientStoreRating toDomain(StoreRatingEntity entity) {
    return PatientStoreRating.reconstitute(
        PatientId.of(entity.getId().getPatientId()),
        OpticalStoreId.of(entity.getId().getOpticalStoreId()),
        StoreRating.of(entity.getScore()),
        entity.getComment(),
        entity.getCreatedAt());
  }

  public StoreRatingEntity toEntity(PatientStoreRating rating) {
    StoreRatingEntity entity = new StoreRatingEntity();
    entity.setId(new PatientStoreKey(
        rating.patientId().value(), rating.opticalStoreId().value()));
    entity.setScore(rating.score().value());
    entity.setComment(rating.comment());
    entity.setCreatedAt(rating.createdAt());
    return entity;
  }
}
