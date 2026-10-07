package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.model.AppointmentStatus;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import com.optiflow.booking.infrastructure.persistence.entity.AppointmentEntity;
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
