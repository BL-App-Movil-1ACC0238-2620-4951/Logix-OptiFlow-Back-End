package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentStatus;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.AppointmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

  public Appointment toDomain(AppointmentEntity entity) {
    return Appointment.reconstitute(
        AppointmentId.of(entity.getId()),
        PatientId.of(entity.getPatientId()),
        OpticalStoreId.of(entity.getOpticalStoreId()),
        TimeSlotId.of(entity.getTimeSlotId()),
        AppointmentStatus.valueOf(entity.getStatus()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public AppointmentEntity toEntity(Appointment appointment) {
    AppointmentEntity entity = new AppointmentEntity();
    entity.setId(appointment.id().value());
    entity.setPatientId(appointment.patientId().value());
    entity.setOpticalStoreId(appointment.opticalStoreId().value());
    entity.setTimeSlotId(appointment.timeSlotId().value());
    entity.setStatus(appointment.status().name());
    entity.setCreatedAt(appointment.createdAt());
    entity.setUpdatedAt(appointment.updatedAt());
    return entity;
  }
}
