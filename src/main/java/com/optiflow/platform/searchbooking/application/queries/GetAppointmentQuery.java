package com.optiflow.platform.searchbooking.application.queries;

import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;

public record GetAppointmentQuery(AppointmentId appointmentId) {
}
