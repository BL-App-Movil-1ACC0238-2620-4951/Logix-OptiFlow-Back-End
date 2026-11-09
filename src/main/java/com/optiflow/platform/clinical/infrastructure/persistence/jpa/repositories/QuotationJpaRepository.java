package com.optiflow.platform.clinical.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.QuotationEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationJpaRepository extends JpaRepository<QuotationEntity, UUID> {

  List<QuotationEntity> findByClinicalRecordIdIn(Collection<UUID> clinicalRecordIds);
}
