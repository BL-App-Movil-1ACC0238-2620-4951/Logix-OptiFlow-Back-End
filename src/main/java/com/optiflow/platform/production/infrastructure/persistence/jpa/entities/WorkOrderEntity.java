package com.optiflow.platform.production.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
public class WorkOrderEntity {

  @Id
  private UUID id;

  @Column(name = "sale_id", nullable = false, unique = true)
  private UUID saleId;

  @Column(name = "patient_id", nullable = false)
  private UUID patientId;

  @Column(name = "optical_store_id")
  private UUID opticalStoreId;

  @Column(name = "technician_id")
  private UUID technicianId;

  @Column(name = "technician_name", length = 120)
  private String technicianName;

  @Column(name = "laboratory_id")
  private UUID laboratoryId;

  @Column(name = "laboratory_name", length = 120)
  private String laboratoryName;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(name = "estimated_delivery_date", nullable = false)
  private LocalDate estimatedDeliveryDate;

  @Column(name = "delay_reason", length = 255)
  private String delayReason;

  @Column(name = "delay_reported_at")
  private Instant delayReportedAt;

  @Column(name = "delay_new_estimated_date")
  private LocalDate delayNewEstimatedDate;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "delivered_at")
  private Instant deliveredAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getSaleId() {
    return saleId;
  }

  public void setSaleId(UUID saleId) {
    this.saleId = saleId;
  }

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID patientId) {
    this.patientId = patientId;
  }

  public UUID getOpticalStoreId() {
    return opticalStoreId;
  }

  public void setOpticalStoreId(UUID opticalStoreId) {
    this.opticalStoreId = opticalStoreId;
  }

  public UUID getTechnicianId() {
    return technicianId;
  }

  public void setTechnicianId(UUID technicianId) {
    this.technicianId = technicianId;
  }

  public String getTechnicianName() {
    return technicianName;
  }

  public void setTechnicianName(String technicianName) {
    this.technicianName = technicianName;
  }

  public UUID getLaboratoryId() {
    return laboratoryId;
  }

  public void setLaboratoryId(UUID laboratoryId) {
    this.laboratoryId = laboratoryId;
  }

  public String getLaboratoryName() {
    return laboratoryName;
  }

  public void setLaboratoryName(String laboratoryName) {
    this.laboratoryName = laboratoryName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getEstimatedDeliveryDate() {
    return estimatedDeliveryDate;
  }

  public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
    this.estimatedDeliveryDate = estimatedDeliveryDate;
  }

  public String getDelayReason() {
    return delayReason;
  }

  public void setDelayReason(String delayReason) {
    this.delayReason = delayReason;
  }

  public Instant getDelayReportedAt() {
    return delayReportedAt;
  }

  public void setDelayReportedAt(Instant delayReportedAt) {
    this.delayReportedAt = delayReportedAt;
  }

  public LocalDate getDelayNewEstimatedDate() {
    return delayNewEstimatedDate;
  }

  public void setDelayNewEstimatedDate(LocalDate delayNewEstimatedDate) {
    this.delayNewEstimatedDate = delayNewEstimatedDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Instant getDeliveredAt() {
    return deliveredAt;
  }

  public void setDeliveredAt(Instant deliveredAt) {
    this.deliveredAt = deliveredAt;
  }
}
