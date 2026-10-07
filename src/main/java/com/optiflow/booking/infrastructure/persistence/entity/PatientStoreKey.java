package com.optiflow.booking.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class PatientStoreKey implements Serializable {

  @Column(name = "patient_id", nullable = false)
  private UUID patientId;

  @Column(name = "optical_store_id", nullable = false)
  private UUID opticalStoreId;

  public PatientStoreKey() {
  }

  public PatientStoreKey(UUID patientId, UUID opticalStoreId) {
    this.patientId = patientId;
    this.opticalStoreId = opticalStoreId;
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

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof PatientStoreKey key)) {
      return false;
    }
    return patientId.equals(key.patientId) && opticalStoreId.equals(key.opticalStoreId);
  }

  @Override
  public int hashCode() {
    return patientId.hashCode() * 31 + opticalStoreId.hashCode();
  }
}
