package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.QuotationItemEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationItemJpaRepository extends JpaRepository<QuotationItemEntity, UUID> {

  List<QuotationItemEntity> findByQuotationIdOrderByLineNumber(UUID quotationId);
}
