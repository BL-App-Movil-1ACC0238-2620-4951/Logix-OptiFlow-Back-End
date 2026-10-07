package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.TechnicianEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianJpaRepository
    extends JpaRepository<TechnicianEntity, UUID> {

  List<TechnicianEntity> findAllByOrderByName();
}
