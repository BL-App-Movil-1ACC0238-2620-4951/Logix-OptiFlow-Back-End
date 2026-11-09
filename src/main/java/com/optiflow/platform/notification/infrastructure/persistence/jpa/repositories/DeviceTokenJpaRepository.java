package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.DeviceTokenEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTokenJpaRepository extends JpaRepository<DeviceTokenEntity, UUID> {

  Optional<DeviceTokenEntity> findByToken(String token);

  List<DeviceTokenEntity> findByPatientId(UUID patientId);

  void deleteByToken(String token);
}
