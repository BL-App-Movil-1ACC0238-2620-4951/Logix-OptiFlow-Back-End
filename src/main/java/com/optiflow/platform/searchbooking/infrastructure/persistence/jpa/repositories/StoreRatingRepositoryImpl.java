package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.repositories.StoreRatingRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.StoreRatingMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StoreRatingRepositoryImpl implements StoreRatingRepository {

  private final StoreRatingJpaRepository storeRatingJpaRepository;
  private final StoreRatingMapper storeRatingMapper;

  public StoreRatingRepositoryImpl(
      StoreRatingJpaRepository storeRatingJpaRepository, StoreRatingMapper storeRatingMapper) {
    this.storeRatingJpaRepository = storeRatingJpaRepository;
    this.storeRatingMapper = storeRatingMapper;
  }

  @Override
  public void save(PatientStoreRating rating) {
    storeRatingJpaRepository.save(storeRatingMapper.toEntity(rating));
  }

  @Override
  public Optional<PatientStoreRating> find(PatientId patientId, OpticalStoreId opticalStoreId) {
    return storeRatingJpaRepository
        .findById(new PatientStoreKey(patientId.value(), opticalStoreId.value()))
        .map(storeRatingMapper::toDomain);
  }

  @Override
  public List<PatientStoreRating> findByStore(OpticalStoreId opticalStoreId) {
    return storeRatingJpaRepository.findByIdOpticalStoreId(opticalStoreId.value()).stream()
        .map(storeRatingMapper::toDomain)
        .toList();
  }
}
