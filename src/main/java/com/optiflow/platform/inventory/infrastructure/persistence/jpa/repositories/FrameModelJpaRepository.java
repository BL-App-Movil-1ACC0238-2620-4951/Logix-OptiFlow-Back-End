package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.FrameModelEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FrameModelJpaRepository extends JpaRepository<FrameModelEntity, UUID> {

  Optional<FrameModelEntity> findBySku(String sku);

  List<FrameModelEntity> findAllByOrderByName();
}
