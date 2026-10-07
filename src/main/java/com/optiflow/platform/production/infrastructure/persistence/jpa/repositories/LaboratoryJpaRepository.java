package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.LaboratoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryJpaRepository
    extends JpaRepository<LaboratoryEntity, UUID> {

  List<LaboratoryEntity> findAllByOrderByName();
}
