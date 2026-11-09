package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.PatientEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, UUID> {

  Optional<PatientEntity> findByEmail(String email);

  @Query("""
      select patient from PatientEntity patient
      where lower(patient.fullName) like concat('%', :name, '%')
        and lower(patient.email) like concat('%', :email, '%')
      order by patient.fullName
      """)
  List<PatientEntity> search(@Param("name") String name, @Param("email") String email);

  @Query("""
      select patient.id from PatientEntity patient
      where patient.birthDate is not null
        and extract(month from patient.birthDate) = :month
        and extract(day from patient.birthDate) = :day
      """)
  List<UUID> findIdsByBirthMonthAndDay(@Param("month") int month, @Param("day") int day);
}
