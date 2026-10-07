package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;

public record BookAppointmentCommand(
    PatientId patientId, OpticalStoreId opticalStoreId, TimeSlotId timeSlotId) {
}
