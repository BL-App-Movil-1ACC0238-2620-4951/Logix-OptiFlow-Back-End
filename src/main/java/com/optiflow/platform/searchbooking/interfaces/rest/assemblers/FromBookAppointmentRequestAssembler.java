package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.commands.BookAppointmentCommand;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.BookAppointmentRequest;
import org.springframework.stereotype.Component;

@Component
public class FromBookAppointmentRequestAssembler {

  public BookAppointmentCommand toCommand(BookAppointmentRequest request) {
    return new BookAppointmentCommand(
        PatientId.of(request.patientId()),
        OpticalStoreId.of(request.opticalStoreId()),
        TimeSlotId.of(request.timeSlotId()));
  }
}
