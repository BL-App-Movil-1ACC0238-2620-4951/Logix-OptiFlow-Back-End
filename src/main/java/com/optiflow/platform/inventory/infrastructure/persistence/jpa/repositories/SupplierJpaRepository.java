package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.SupplierEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierJpaRepository extends JpaRepository<SupplierEntity, UUID> {

  List<SupplierEntity> findAllByOrderByName();
}
