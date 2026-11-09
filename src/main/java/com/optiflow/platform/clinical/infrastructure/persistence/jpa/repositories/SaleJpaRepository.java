package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.SaleEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {

  boolean existsByQuotationId(UUID quotationId);

  List<SaleEntity> findByPatientId(UUID patientId);

  List<SaleEntity> findByClosedAtGreaterThanEqualAndClosedAtLessThanOrderByClosedAt(
      Instant from, Instant to);
}
