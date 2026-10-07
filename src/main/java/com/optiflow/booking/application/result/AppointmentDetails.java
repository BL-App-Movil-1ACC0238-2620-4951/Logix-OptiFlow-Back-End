package com.optiflow.booking.application.result;

import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.model.TimeSlot;

public record AppointmentDetails(Appointment appointment, TimeSlot timeSlot) {
}
