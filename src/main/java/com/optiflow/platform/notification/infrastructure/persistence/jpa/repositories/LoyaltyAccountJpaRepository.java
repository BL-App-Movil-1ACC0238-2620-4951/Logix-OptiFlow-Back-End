package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.LoyaltyAccountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoyaltyAccountJpaRepository extends JpaRepository<LoyaltyAccountEntity, UUID> {
}
