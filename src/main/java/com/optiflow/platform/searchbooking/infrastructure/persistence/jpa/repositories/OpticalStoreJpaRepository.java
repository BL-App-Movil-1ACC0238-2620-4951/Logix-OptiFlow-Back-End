package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.OpticalStoreEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpticalStoreJpaRepository extends JpaRepository<OpticalStoreEntity, UUID> {
}
