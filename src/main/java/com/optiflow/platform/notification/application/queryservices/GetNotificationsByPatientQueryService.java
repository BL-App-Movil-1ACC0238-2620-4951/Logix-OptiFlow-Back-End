package com.optiflow.platform.notification.application.queryservices;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationsByPatientQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationsByPatientQueryService {

  private final NotificationRepository notificationRepository;
  private final Clock clock;

  public GetNotificationsByPatientQueryService(
      NotificationRepository notificationRepository, Clock clock) {
    this.notificationRepository = notificationRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public List<Notification> handle(GetNotificationsByPatientQuery query) {
    Instant now = clock.instant();
    return notificationRepository.findByPatientId(PatientId.of(query.patientId())).stream()
        .filter(notification -> notification.isDue(now))
        .sorted(Comparator.comparing(Notification::date).reversed())
        .toList();
  }
}
