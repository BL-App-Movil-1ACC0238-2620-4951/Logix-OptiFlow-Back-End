package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotStatus;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.TimeSlotEntity;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {

  public TimeSlot toDomain(TimeSlotEntity entity) {
    return TimeSlot.reconstitute(
        TimeSlotId.of(entity.getId()),
        OpticalStoreId.of(entity.getOpticalStoreId()),
        entity.getStartDateTime(),
        entity.getEndDateTime(),
        TimeSlotStatus.valueOf(entity.getStatus()));
  }

  public TimeSlotEntity toEntity(TimeSlot timeSlot) {
    TimeSlotEntity entity = new TimeSlotEntity();
    entity.setId(timeSlot.id().value());
    entity.setOpticalStoreId(timeSlot.opticalStoreId().value());
    entity.setStartDateTime(timeSlot.startDateTime());
    entity.setEndDateTime(timeSlot.endDateTime());
    entity.setStatus(timeSlot.status().name());
    return entity;
  }
}
