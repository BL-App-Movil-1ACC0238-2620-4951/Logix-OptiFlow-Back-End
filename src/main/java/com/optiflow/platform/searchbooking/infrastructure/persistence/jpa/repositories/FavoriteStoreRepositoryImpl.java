package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.repositories.FavoriteStoreRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.FavoriteStoreMapper;
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
