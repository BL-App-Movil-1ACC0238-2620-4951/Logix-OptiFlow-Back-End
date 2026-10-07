package com.optiflow.booking.infrastructure.persistence;

import com.optiflow.booking.domain.model.FavoriteStore;
import com.optiflow.booking.domain.repository.FavoriteStoreRepository;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
import com.optiflow.booking.infrastructure.persistence.jpa.FavoriteStoreJpaRepository;
import com.optiflow.booking.infrastructure.persistence.mapper.FavoriteStoreMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class FavoriteStoreRepositoryImpl implements FavoriteStoreRepository {

  private final FavoriteStoreJpaRepository favoriteStoreJpaRepository;
  private final FavoriteStoreMapper favoriteStoreMapper;

  public FavoriteStoreRepositoryImpl(
      FavoriteStoreJpaRepository favoriteStoreJpaRepository,
      FavoriteStoreMapper favoriteStoreMapper) {
    this.favoriteStoreJpaRepository = favoriteStoreJpaRepository;
    this.favoriteStoreMapper = favoriteStoreMapper;
  }

  @Override
  public void save(FavoriteStore favoriteStore) {
    favoriteStoreJpaRepository.save(favoriteStoreMapper.toEntity(favoriteStore));
  }

  @Override
  public Optional<FavoriteStore> find(PatientId patientId, OpticalStoreId opticalStoreId) {
    return favoriteStoreJpaRepository
        .findById(new PatientStoreKey(patientId.value(), opticalStoreId.value()))
        .map(favoriteStoreMapper::toDomain);
  }
}
