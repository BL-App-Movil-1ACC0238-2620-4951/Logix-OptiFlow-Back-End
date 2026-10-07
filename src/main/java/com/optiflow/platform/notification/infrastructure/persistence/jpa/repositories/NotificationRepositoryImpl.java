package com.optiflow.platform.notification.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers.NotificationMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

  private final NotificationJpaRepository notificationJpaRepository;
  private final NotificationMapper notificationMapper;

  public NotificationRepositoryImpl(
      NotificationJpaRepository notificationJpaRepository, NotificationMapper notificationMapper) {
    this.notificationJpaRepository = notificationJpaRepository;
    this.notificationMapper = notificationMapper;
  }

  @Override
  public void save(Notification notification) {
    notificationJpaRepository.save(notificationMapper.toEntity(notification));
  }

  @Override
  public Optional<Notification> findById(NotificationId id) {
    return notificationJpaRepository.findById(id.value()).map(notificationMapper::toDomain);
  }

  @Override
  public List<Notification> findByPatientId(PatientId patientId) {
    return notificationJpaRepository.findByPatientIdOrderByCreatedAtDesc(patientId.value()).stream()
        .map(notificationMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByPatientIdAndAppointmentId(
      PatientId patientId, AppointmentId appointmentId) {
    return notificationJpaRepository.existsByPatientIdAndAppointmentId(
        patientId.value(), appointmentId.value());
  }
}
