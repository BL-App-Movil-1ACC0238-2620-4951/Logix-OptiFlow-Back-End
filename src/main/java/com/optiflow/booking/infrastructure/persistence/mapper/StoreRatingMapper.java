package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.StoreRating;
import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
import com.optiflow.booking.infrastructure.persistence.entity.StoreRatingEntity;
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
