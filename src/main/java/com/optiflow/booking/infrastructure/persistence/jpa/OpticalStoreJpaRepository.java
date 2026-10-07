package com.optiflow.booking.infrastructure.persistence.jpa;

import com.optiflow.booking.infrastructure.persistence.entity.OpticalStoreEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpticalStoreJpaRepository extends JpaRepository<OpticalStoreEntity, UUID> {
}
