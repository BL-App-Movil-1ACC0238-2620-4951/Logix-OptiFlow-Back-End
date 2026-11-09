package com.optiflow.platform.notification.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationId;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationMessage;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.NotificationType;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.notification.domain.model.valueobjects.PushStatus;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.notification.infrastructure.persistence.jpa.entities.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

  public Notification toDomain(NotificationEntity entity) {
    return Notification.reconstitute(
        NotificationId.of(entity.getId()),
        PatientId.of(entity.getPatientId()),
        NotificationType.valueOf(entity.getType()),
        new NotificationMessage(entity.getTitle(), entity.getBody()),
        entity.getAppointmentId() == null ? null : AppointmentId.of(entity.getAppointmentId()),
        entity.getWorkOrderId() == null ? null : WorkOrderId.of(entity.getWorkOrderId()),
        NotificationStatus.valueOf(entity.getStatus()),
        PushStatus.valueOf(entity.getPushStatus()),
        entity.getScheduledAt(),
        entity.getCreatedAt(),
        entity.getReadAt());
  }

  public NotificationEntity toEntity(Notification notification) {
    NotificationEntity entity = new NotificationEntity();
    entity.setId(notification.id().value());
    entity.setPatientId(notification.patientId().value());
    entity.setType(notification.type().name());
    entity.setTitle(notification.message().title());
    entity.setBody(notification.message().body());
    entity.setAppointmentId(notification.appointmentId().map(AppointmentId::value).orElse(null));
    entity.setWorkOrderId(notification.workOrderId().map(WorkOrderId::value).orElse(null));
    entity.setStatus(notification.status().name());
    entity.setPushStatus(notification.pushStatus().name());
    entity.setScheduledAt(notification.scheduledAt().orElse(null));
    entity.setCreatedAt(notification.createdAt());
    entity.setReadAt(notification.readAt().orElse(null));
    return entity;
  }
}
