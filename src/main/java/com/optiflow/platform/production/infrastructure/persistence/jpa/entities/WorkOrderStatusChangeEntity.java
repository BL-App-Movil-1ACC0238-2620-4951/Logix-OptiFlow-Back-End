package com.optiflow.platform.production.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "work_order_status_history")
public class WorkOrderStatusChangeEntity {

  @EmbeddedId
  private WorkOrderStatusChangeKey id;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "changed_at", nullable = false)
  private Instant changedAt;

  public WorkOrderStatusChangeKey getId() {
    return id;
  }

  public void setId(WorkOrderStatusChangeKey id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Instant getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(Instant changedAt) {
    this.changedAt = changedAt;
  }
}
