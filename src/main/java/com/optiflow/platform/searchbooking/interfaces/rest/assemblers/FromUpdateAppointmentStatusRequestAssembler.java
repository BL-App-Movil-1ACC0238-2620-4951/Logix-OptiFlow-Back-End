package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.AppointmentStatusCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.CancelAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.CompleteAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.AppointmentStatus;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.UpdateAppointmentStatusRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromUpdateAppointmentStatusRequestAssembler {

  public AppointmentStatusCommand toCommand(
      UUID appointmentId, UpdateAppointmentStatusRequest request) {
    AppointmentId id = AppointmentId.of(appointmentId);
    AppointmentStatus status = parseAppointmentStatus(request.status());
    return switch (status) {
      case CANCELLED -> new CancelAppointmentCommand(id);
      case COMPLETED -> new CompleteAppointmentCommand(id);
      case PENDING, CONFIRMED ->
          throw new DomainException("Appointment status cannot be changed to " + status + ".", 400);
    };
  }

  private static AppointmentStatus parseAppointmentStatus(String status) {
    try {
      return AppointmentStatus.valueOf(status.trim().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new DomainException("Appointment status must be CANCELLED or COMPLETED.", 400);
    }
  }
}
