package com.optiflow.platform.notification.interfaces.rest.assemblers;

import com.optiflow.platform.notification.domain.model.aggregates.Notification;
import com.optiflow.platform.notification.domain.model.entities.DeviceToken;
import com.optiflow.platform.notification.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.notification.domain.model.valueobjects.WorkOrderId;
import com.optiflow.platform.notification.interfaces.rest.resources.DeviceTokenResponse;
import com.optiflow.platform.notification.interfaces.rest.resources.NotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationResourceAssembler {

  public DeviceTokenResponse toResponse(DeviceToken deviceToken) {
    return new DeviceTokenResponse(
        deviceToken.id().value(), deviceToken.platform().name(), deviceToken.updatedAt());
  }

  public NotificationResponse toResponse(Notification notification) {
    return new NotificationResponse(
        notification.id().value(),
        notification.patientId().value(),
        notification.type().name(),
        notification.message().title(),
        notification.message().body(),
        notification.status().name(),
        notification.createdAt(),
        notification.readAt().orElse(null),
        notification.appointmentId().map(AppointmentId::value).orElse(null),
        notification.workOrderId().map(WorkOrderId::value).orElse(null));
  }
}
