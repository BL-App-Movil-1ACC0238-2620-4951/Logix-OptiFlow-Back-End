package com.optiflow.platform.notification.domain.repositories;

import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  void save(Notification notification);

  Optional<Notification> findById(NotificationId id);

  List<Notification> findByPatientId(PatientId patientId);

  boolean existsByPatientIdAndAppointmentId(PatientId patientId, AppointmentId appointmentId);
}
