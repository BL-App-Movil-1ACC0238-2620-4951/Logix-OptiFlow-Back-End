package com.optiflow.platform.production.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "work_order_lenses")
public class WorkOrderLensEntity {

  @Id
  private UUID id;

  @Column(name = "work_order_id", nullable = false)
  private UUID workOrderId;

  @Column(name = "line_number", nullable = false)
  private Integer lineNumber;

  @Column(nullable = false, length = 500)
  private String specifications;

  @Column(nullable = false)
  private Boolean completed;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getWorkOrderId() {
    return workOrderId;
  }

  public void setWorkOrderId(UUID workOrderId) {
    this.workOrderId = workOrderId;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getSpecifications() {
    return specifications;
  }

  public void setSpecifications(String specifications) {
    this.specifications = specifications;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }
}
