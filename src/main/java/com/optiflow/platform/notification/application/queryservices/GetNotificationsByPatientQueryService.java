package com.optiflow.platform.notification.application.queryservices;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
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
