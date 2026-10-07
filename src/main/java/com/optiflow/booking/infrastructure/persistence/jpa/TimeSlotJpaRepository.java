package com.optiflow.booking.infrastructure.persistence.jpa;

import com.optiflow.booking.infrastructure.persistence.entity.TimeSlotEntity;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeSlotJpaRepository extends JpaRepository<TimeSlotEntity, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select slot from TimeSlotEntity slot where slot.id = :id")
  Optional<TimeSlotEntity> findByIdForUpdate(@Param("id") UUID id);

  @Query("""
      select slot from TimeSlotEntity slot
      where slot.opticalStoreId = :storeId
        and slot.status = 'AVAILABLE'
        and slot.startDateTime > :now
      order by slot.startDateTime
      """)
  List<TimeSlotEntity> findAvailableByStore(
      @Param("storeId") UUID storeId, @Param("now") Instant now);
}
