package com.optiflow.platform.production.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.production.infrastructure.persistence.jpa.entities.WorkOrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkOrderJpaRepository
    extends JpaRepository<WorkOrderEntity, UUID>, JpaSpecificationExecutor<WorkOrderEntity> {

  boolean existsBySaleId(UUID saleId);

  List<WorkOrderEntity> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
}
