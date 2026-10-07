package com.optiflow.platform.searchbooking.domain.entities;

import com.optiflow.platform.searchbooking.domain.exceptions.TimeSlotUnavailableException;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotStatus;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;

public class TimeSlot {

  private final TimeSlotId id;
  private final OpticalStoreId opticalStoreId;
  private final Instant startDateTime;
  private final Instant endDateTime;
  private TimeSlotStatus status;

  private TimeSlot(
      TimeSlotId id,
      OpticalStoreId opticalStoreId,
      Instant startDateTime,
      Instant endDateTime,
      TimeSlotStatus status) {
    if (!endDateTime.isAfter(startDateTime)) {
      throw new DomainException("Time slot end must be after its start.", 400);
    }
    this.id = id;
    this.opticalStoreId = opticalStoreId;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.status = status;
  }

  public static TimeSlot publish(
      TimeSlotId id, OpticalStoreId opticalStoreId, Instant startDateTime, Instant endDateTime) {
    return new TimeSlot(id, opticalStoreId, startDateTime, endDateTime, TimeSlotStatus.AVAILABLE);
  }

  public static TimeSlot reconstitute(
      TimeSlotId id,
      OpticalStoreId opticalStoreId,
      Instant startDateTime,
      Instant endDateTime,
      TimeSlotStatus status) {
    return new TimeSlot(id, opticalStoreId, startDateTime, endDateTime, status);
  }

  public boolean isAvailable(Instant now) {
    return status == TimeSlotStatus.AVAILABLE && startDateTime.isAfter(now);
  }

  public void reserve(Instant now) {
    if (!isAvailable(now)) {
      throw new TimeSlotUnavailableException();
    }
    status = TimeSlotStatus.RESERVED;
  }

  public void release() {
    status = TimeSlotStatus.AVAILABLE;
  }

  public TimeSlotId id() {
    return id;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public Instant startDateTime() {
    return startDateTime;
  }

  public Instant endDateTime() {
    return endDateTime;
  }

  public TimeSlotStatus status() {
    return status;
  }
}
