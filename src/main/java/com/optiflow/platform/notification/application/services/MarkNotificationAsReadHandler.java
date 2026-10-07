package com.optiflow.platform.notification.application.services;

import com.optiflow.platform.notification.application.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.domain.entities.Notification;
import com.optiflow.platform.notification.domain.exceptions.NotificationNotFoundException;
import com.optiflow.platform.notification.domain.repositories.NotificationRepository;
import com.optiflow.platform.notification.domain.valueobjects.NotificationId;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkNotificationAsReadHandler {

  private final NotificationRepository notificationRepository;
  private final Clock clock;

  public MarkNotificationAsReadHandler(
      NotificationRepository notificationRepository, Clock clock) {
    this.notificationRepository = notificationRepository;
    this.clock = clock;
  }

  @Transactional
  public Notification handle(MarkNotificationAsReadCommand command) {
    NotificationId notificationId = NotificationId.of(command.notificationId());
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationNotFoundException::new);
    notification.markAsRead(clock.instant());
    notificationRepository.save(notification);
    return notification;
  }
}
