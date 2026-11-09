package com.optiflow.platform.notification.interfaces.rest.assemblers;

import com.optiflow.platform.notification.domain.model.commands.MarkNotificationAsReadCommand;
import com.optiflow.platform.notification.interfaces.rest.resources.UpdateNotificationRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Turns {@code {"read": true}} into the command that marks the notification as read. */
@Component
public class FromUpdateNotificationRequestAssembler {

  public MarkNotificationAsReadCommand toCommand(UUID id, UpdateNotificationRequest request) {
    return new MarkNotificationAsReadCommand(id);
  }
}
