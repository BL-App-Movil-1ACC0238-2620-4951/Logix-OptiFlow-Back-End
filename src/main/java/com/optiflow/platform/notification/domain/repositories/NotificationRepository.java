package com.optiflow.platform.notification.domain.repositories;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  void save(Notification notification);

  Optional<Notification> findById(NotificationId id);

  List<Notification> findByPatientId(PatientId patientId);

  boolean existsByPatientIdAndAppointmentId(PatientId patientId, AppointmentId appointmentId);
}
