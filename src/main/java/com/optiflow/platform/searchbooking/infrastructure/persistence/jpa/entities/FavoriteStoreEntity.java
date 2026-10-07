package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "favorite_stores")
public class FavoriteStoreEntity {

  @EmbeddedId
  private PatientStoreKey id;

  @Column(name = "saved_at", nullable = false)
  private Instant savedAt;

  public PatientStoreKey getId() {
    return id;
  }

  public void setId(PatientStoreKey id) {
    this.id = id;
  }

  public Instant getSavedAt() {
    return savedAt;
  }

  public void setSavedAt(Instant savedAt) {
    this.savedAt = savedAt;
  }
}
