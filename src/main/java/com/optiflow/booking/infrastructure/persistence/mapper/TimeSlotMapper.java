package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.model.TimeSlotStatus;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import com.optiflow.booking.infrastructure.persistence.entity.TimeSlotEntity;
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
