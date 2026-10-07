package com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "medical_histories")
public class MedicalHistoryEntity {

  @Id
  @Column(name = "clinical_record_id")
  private UUID clinicalRecordId;

  @Column(length = 1000)
  private String allergies;

  @Column(name = "previous_conditions", length = 1000)
  private String previousConditions;

  @Column(name = "family_ocular_history", length = 1000)
  private String familyOcularHistory;

  @Column(name = "last_updated", nullable = false)
  private Instant lastUpdated;

  public UUID getClinicalRecordId() {
    return clinicalRecordId;
  }

  public void setClinicalRecordId(UUID clinicalRecordId) {
    this.clinicalRecordId = clinicalRecordId;
  }

  public String getAllergies() {
    return allergies;
  }

  public void setAllergies(String allergies) {
    this.allergies = allergies;
  }

  public String getPreviousConditions() {
    return previousConditions;
  }

  public void setPreviousConditions(String previousConditions) {
    this.previousConditions = previousConditions;
  }

  public String getFamilyOcularHistory() {
    return familyOcularHistory;
  }

  public void setFamilyOcularHistory(String familyOcularHistory) {
    this.familyOcularHistory = familyOcularHistory;
  }

  public Instant getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Instant lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
}
