package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.AppointmentEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {

  List<AppointmentEntity> findByPatientId(UUID patientId);

  @Query("""
      select appointment from AppointmentEntity appointment
      where appointment.timeSlotId = :timeSlotId
        and appointment.status in ('PENDING', 'CONFIRMED')
      """)
  Optional<AppointmentEntity> findActiveByTimeSlot(@Param("timeSlotId") UUID timeSlotId);
}
