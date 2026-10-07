package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.StoreRatingEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRatingJpaRepository extends JpaRepository<StoreRatingEntity, PatientStoreKey> {

  List<StoreRatingEntity> findByIdOpticalStoreId(UUID opticalStoreId);
}
