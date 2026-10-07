package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "time_slots")
public class TimeSlotEntity {

  @Id
  private UUID id;

  @Column(name = "optical_store_id", nullable = false)
  private UUID opticalStoreId;

  @Column(name = "start_date_time", nullable = false)
  private Instant startDateTime;

  @Column(name = "end_date_time", nullable = false)
  private Instant endDateTime;

  @Column(nullable = false, length = 20)
  private String status;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOpticalStoreId() {
    return opticalStoreId;
  }

  public void setOpticalStoreId(UUID opticalStoreId) {
    this.opticalStoreId = opticalStoreId;
  }

  public Instant getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Instant startDateTime) {
    this.startDateTime = startDateTime;
  }

  public Instant getEndDateTime() {
    return endDateTime;
  }

  public void setEndDateTime(Instant endDateTime) {
    this.endDateTime = endDateTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
