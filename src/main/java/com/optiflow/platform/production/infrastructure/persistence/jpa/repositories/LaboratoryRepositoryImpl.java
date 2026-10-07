package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.domain.entities.Laboratory;
import com.optiflow.platform.production.domain.repositories.LaboratoryRepository;
import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.production.infrastructure.persistence.jpa.mappers.ProductionCatalogMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class LaboratoryRepositoryImpl implements LaboratoryRepository {

  private final LaboratoryJpaRepository laboratoryJpaRepository;
  private final ProductionCatalogMapper catalogMapper;

  public LaboratoryRepositoryImpl(
      LaboratoryJpaRepository laboratoryJpaRepository, ProductionCatalogMapper catalogMapper) {
    this.laboratoryJpaRepository = laboratoryJpaRepository;
    this.catalogMapper = catalogMapper;
  }

  @Override
  public void save(Laboratory laboratory) {
    laboratoryJpaRepository.save(catalogMapper.toEntity(laboratory));
  }

  @Override
  public Optional<Laboratory> findById(LaboratoryId id) {
    return laboratoryJpaRepository.findById(id.value()).map(catalogMapper::toDomain);
  }

  @Override
  public List<Laboratory> findAll() {
    return laboratoryJpaRepository.findAllByOrderByName().stream()
        .map(catalogMapper::toDomain)
        .toList();
  }

  @Override
  public boolean isEmpty() {
    return laboratoryJpaRepository.count() == 0;
  }
}
