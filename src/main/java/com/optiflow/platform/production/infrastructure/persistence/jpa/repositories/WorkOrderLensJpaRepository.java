package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderLensEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderLensJpaRepository
    extends JpaRepository<WorkOrderLensEntity, UUID> {

  List<WorkOrderLensEntity> findByWorkOrderIdOrderByLineNumber(UUID workOrderId);
}
