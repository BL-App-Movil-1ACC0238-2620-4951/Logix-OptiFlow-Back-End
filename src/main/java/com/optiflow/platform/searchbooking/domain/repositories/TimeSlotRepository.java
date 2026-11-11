package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.TimeSlotId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

  void save(TimeSlot timeSlot);

  Optional<TimeSlot> findById(TimeSlotId id);

  Optional<TimeSlot> findByIdForUpdate(TimeSlotId id);

  List<TimeSlot> findAvailableByStore(OpticalStoreId opticalStoreId, Instant now);

  boolean existsOverlapping(OpticalStoreId opticalStoreId, Instant start, Instant end);
}
