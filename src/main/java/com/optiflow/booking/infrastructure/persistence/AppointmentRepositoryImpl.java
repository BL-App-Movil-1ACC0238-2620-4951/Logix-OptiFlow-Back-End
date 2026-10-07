package com.optiflow.booking.infrastructure.persistence;

import com.optiflow.booking.domain.model.Appointment;
import com.optiflow.booking.domain.repository.AppointmentRepository;
import com.optiflow.booking.domain.vo.AppointmentId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import com.optiflow.booking.infrastructure.persistence.jpa.AppointmentJpaRepository;
import com.optiflow.booking.infrastructure.persistence.mapper.AppointmentMapper;
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
