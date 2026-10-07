package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.entities.Appointment;
import com.optiflow.platform.searchbooking.domain.repositories.AppointmentRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.AppointmentId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.AppointmentMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepositoryImpl implements AppointmentRepository {

  private final AppointmentJpaRepository appointmentJpaRepository;
  private final AppointmentMapper appointmentMapper;

  public AppointmentRepositoryImpl(
      AppointmentJpaRepository appointmentJpaRepository, AppointmentMapper appointmentMapper) {
    this.appointmentJpaRepository = appointmentJpaRepository;
    this.appointmentMapper = appointmentMapper;
  }

  @Override
  public void save(Appointment appointment) {
    appointmentJpaRepository.save(appointmentMapper.toEntity(appointment));
  }

  @Override
  public Optional<Appointment> findById(AppointmentId id) {
    return appointmentJpaRepository.findById(id.value()).map(appointmentMapper::toDomain);
  }

  @Override
  public List<Appointment> findByPatientId(PatientId patientId) {
    return appointmentJpaRepository.findByPatientId(patientId.value()).stream()
        .map(appointmentMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<Appointment> findActiveByTimeSlot(TimeSlotId timeSlotId) {
    return appointmentJpaRepository.findActiveByTimeSlot(timeSlotId.value())
        .map(appointmentMapper::toDomain);
  }
}
