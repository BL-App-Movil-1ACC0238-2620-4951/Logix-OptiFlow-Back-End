package com.optiflow.platform.notification.support;

import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeNotificationRepository implements NotificationRepository {

  private final Map<NotificationId, Notification> store = new ConcurrentHashMap<>();

  @Override
  public void save(Notification notification) {
    store.put(notification.id(), notification);
  }

  @Override
  public Optional<Notification> findById(NotificationId id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Notification> findByPatientId(PatientId patientId) {
    return store.values().stream()
        .filter(notification -> notification.patientId().equals(patientId))
        .toList();
  }

  @Override
  public boolean existsByPatientIdAndAppointmentId(
      PatientId patientId, AppointmentId appointmentId) {
    return store.values().stream()
        .anyMatch(notification ->
            notification.patientId().equals(patientId)
                && notification.appointmentId().filter(appointmentId::equals).isPresent());
  }

  public List<Notification> findAll() {
    return new ArrayList<>(store.values());
  }
}
