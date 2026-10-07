package com.optiflow.platform.production.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class WorkOrderStatusChangeKey implements Serializable {

  @Column(name = "work_order_id", nullable = false)
  private UUID workOrderId;

  @Column(name = "sequence_number", nullable = false)
  private Integer sequenceNumber;

  public WorkOrderStatusChangeKey() {
  }

  public WorkOrderStatusChangeKey(UUID workOrderId, Integer sequenceNumber) {
    this.workOrderId = workOrderId;
    this.sequenceNumber = sequenceNumber;
  }

  public UUID getWorkOrderId() {
    return workOrderId;
  }

  public void setWorkOrderId(UUID workOrderId) {
    this.workOrderId = workOrderId;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof WorkOrderStatusChangeKey key)) {
      return false;
    }
    return Objects.equals(workOrderId, key.workOrderId)
        && Objects.equals(sequenceNumber, key.sequenceNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(workOrderId, sequenceNumber);
  }
}
