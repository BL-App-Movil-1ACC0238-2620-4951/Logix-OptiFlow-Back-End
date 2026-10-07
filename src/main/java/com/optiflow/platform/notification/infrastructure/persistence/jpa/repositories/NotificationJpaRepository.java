package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.NotificationEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

  List<NotificationEntity> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

  boolean existsByPatientIdAndAppointmentId(UUID patientId, UUID appointmentId);
}
