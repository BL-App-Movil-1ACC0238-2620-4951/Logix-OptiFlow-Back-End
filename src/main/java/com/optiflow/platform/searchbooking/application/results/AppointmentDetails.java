package com.optiflow.platform.searchbooking.application.results;

import com.optiflow.platform.searchbooking.domain.model.aggregates.Appointment;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;

public record AppointmentDetails(Appointment appointment, TimeSlot timeSlot) {
}
