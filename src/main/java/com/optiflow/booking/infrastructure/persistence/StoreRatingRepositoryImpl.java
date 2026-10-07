package com.optiflow.booking.infrastructure.persistence;

import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.repository.StoreRatingRepository;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
import com.optiflow.booking.infrastructure.persistence.jpa.StoreRatingJpaRepository;
import com.optiflow.booking.infrastructure.persistence.mapper.StoreRatingMapper;
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
