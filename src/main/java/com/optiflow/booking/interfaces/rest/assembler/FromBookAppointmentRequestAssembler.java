package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.command.BookAppointmentCommand;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import com.optiflow.booking.interfaces.rest.dto.BookAppointmentRequest;
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
