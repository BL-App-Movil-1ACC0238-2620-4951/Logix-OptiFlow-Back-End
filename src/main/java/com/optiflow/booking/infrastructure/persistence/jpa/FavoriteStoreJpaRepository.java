package com.optiflow.booking.infrastructure.persistence.jpa;

import com.optiflow.booking.infrastructure.persistence.entity.FavoriteStoreEntity;
import com.optiflow.booking.infrastructure.persistence.entity.PatientStoreKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStoreJpaRepository extends JpaRepository<FavoriteStoreEntity, PatientStoreKey> {
}
