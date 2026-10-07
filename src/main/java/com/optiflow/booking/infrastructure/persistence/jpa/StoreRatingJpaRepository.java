package com.optiflow.booking.infrastructure.persistence.jpa;

import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
import com.optiflow.booking.infrastructure.persistence.entity.StoreRatingEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRatingJpaRepository extends JpaRepository<StoreRatingEntity, PatientStoreKey> {

  List<StoreRatingEntity> findByIdOpticalStoreId(UUID opticalStoreId);
}
