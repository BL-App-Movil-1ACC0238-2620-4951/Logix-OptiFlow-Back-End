package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.domain.entities.Technician;
import com.optiflow.platform.production.domain.repositories.TechnicianRepository;
import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.production.infrastructure.persistence.jpa.mappers.ProductionCatalogMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TechnicianRepositoryImpl implements TechnicianRepository {

  private final TechnicianJpaRepository technicianJpaRepository;
  private final ProductionCatalogMapper catalogMapper;

  public TechnicianRepositoryImpl(
      TechnicianJpaRepository technicianJpaRepository, ProductionCatalogMapper catalogMapper) {
    this.technicianJpaRepository = technicianJpaRepository;
    this.catalogMapper = catalogMapper;
  }

  @Override
  public void save(Technician technician) {
    technicianJpaRepository.save(catalogMapper.toEntity(technician));
  }

  @Override
  public Optional<Technician> findById(TechnicianId id) {
    return technicianJpaRepository.findById(id.value()).map(catalogMapper::toDomain);
  }

  @Override
  public List<Technician> findAll() {
    return technicianJpaRepository.findAllByOrderByName().stream()
        .map(catalogMapper::toDomain)
        .toList();
  }

  @Override
  public boolean isEmpty() {
    return technicianJpaRepository.count() == 0;
  }
}
