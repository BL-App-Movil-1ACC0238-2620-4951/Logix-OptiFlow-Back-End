package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.services.OpticalStoreSearchService;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.OpticalStoreMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OpticalStoreRepositoryImpl implements OpticalStoreRepository {

  private final OpticalStoreJpaRepository opticalStoreJpaRepository;
  private final OpticalStoreMapper opticalStoreMapper;
  private final OpticalStoreSearchService opticalStoreSearchService;

  public OpticalStoreRepositoryImpl(
      OpticalStoreJpaRepository opticalStoreJpaRepository,
      OpticalStoreMapper opticalStoreMapper,
      OpticalStoreSearchService opticalStoreSearchService) {
    this.opticalStoreJpaRepository = opticalStoreJpaRepository;
    this.opticalStoreMapper = opticalStoreMapper;
    this.opticalStoreSearchService = opticalStoreSearchService;
  }

  @Override
  public Optional<OpticalStore> findById(OpticalStoreId id) {
    return opticalStoreJpaRepository.findById(id.value()).map(opticalStoreMapper::toDomain);
  }

  @Override
  public List<OpticalStore> search(String name, String address) {
    return opticalStoreSearchService.search(findAll(), name, address);
  }

  @Override
  public List<OpticalStore> filter(String name, String address, BigDecimal minRating) {
    return opticalStoreSearchService.filter(findAll(), name, address, minRating);
  }

  @Override
  public void save(OpticalStore opticalStore) {
    opticalStoreJpaRepository.save(opticalStoreMapper.toEntity(opticalStore));
  }

  @Override
  public boolean isEmpty() {
    return opticalStoreJpaRepository.count() == 0;
  }

  private List<OpticalStore> findAll() {
    return opticalStoreJpaRepository.findAll().stream().map(opticalStoreMapper::toDomain).toList();
  }
}
