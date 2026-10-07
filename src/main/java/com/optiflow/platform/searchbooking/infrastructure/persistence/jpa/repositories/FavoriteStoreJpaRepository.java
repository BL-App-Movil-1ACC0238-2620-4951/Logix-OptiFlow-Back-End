package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.FavoriteStoreEntity;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientStoreKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStoreJpaRepository extends JpaRepository<FavoriteStoreEntity, PatientStoreKey> {
}
