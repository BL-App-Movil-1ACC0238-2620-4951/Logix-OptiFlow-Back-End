package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderStatusChangeEntity;
import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderStatusChangeKey;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderStatusChangeJpaRepository
    extends JpaRepository<WorkOrderStatusChangeEntity, WorkOrderStatusChangeKey> {

  List<WorkOrderStatusChangeEntity> findByIdWorkOrderIdOrderByIdSequenceNumber(UUID workOrderId);
}
