package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;

public record AppointmentDetails(Appointment appointment, TimeSlot timeSlot) {
}
