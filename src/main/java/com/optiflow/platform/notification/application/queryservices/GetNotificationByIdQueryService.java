package com.optiflow.platform.notification.application.queryservices;

import com.optiflow.platform.notification.domain.exceptions.NotificationNotFoundException;
import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.queries.GetNotificationByIdQuery;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationByIdQueryService {

  private final NotificationRepository notificationRepository;

  public GetNotificationByIdQueryService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional(readOnly = true)
  public Notification handle(GetNotificationByIdQuery query) {
    return notificationRepository.findById(NotificationId.of(query.notificationId()))
        .orElseThrow(NotificationNotFoundException::new);
  }
}
