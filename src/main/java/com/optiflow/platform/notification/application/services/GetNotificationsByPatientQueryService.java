package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationsByPatientQueryService {

  private final NotificationRepository notificationRepository;

  public GetNotificationsByPatientQueryService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional(readOnly = true)
  public List<Notification> handle(GetNotificationsByPatientQuery query) {
    return notificationRepository.findByPatientId(PatientId.of(query.patientId()));
  }
}
