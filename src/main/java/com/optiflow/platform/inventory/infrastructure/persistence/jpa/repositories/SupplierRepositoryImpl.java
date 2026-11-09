package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.inventory.domain.repositories.SupplierRepository;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers.SupplierMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

  private final SupplierJpaRepository supplierJpaRepository;
  private final SupplierMapper supplierMapper;

  public SupplierRepositoryImpl(
      SupplierJpaRepository supplierJpaRepository, SupplierMapper supplierMapper) {
    this.supplierJpaRepository = supplierJpaRepository;
    this.supplierMapper = supplierMapper;
  }

  @Override
  public void save(Supplier supplier) {
    supplierJpaRepository.save(supplierMapper.toEntity(supplier));
  }

  @Override
  public Optional<Supplier> findById(SupplierId id) {
    return supplierJpaRepository.findById(id.value()).map(supplierMapper::toDomain);
  }

  @Override
  public List<Supplier> findAll() {
    return supplierJpaRepository.findAllByOrderByName().stream()
        .map(supplierMapper::toDomain)
        .toList();
  }
}
