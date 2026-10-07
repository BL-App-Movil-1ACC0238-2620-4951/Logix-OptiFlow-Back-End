package com.optiflow.booking.domain.repository;

import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

  void save(TimeSlot timeSlot);

  Optional<TimeSlot> findById(TimeSlotId id);

  Optional<TimeSlot> findByIdForUpdate(TimeSlotId id);

  List<TimeSlot> findAvailableByStore(OpticalStoreId opticalStoreId, Instant now);
}
