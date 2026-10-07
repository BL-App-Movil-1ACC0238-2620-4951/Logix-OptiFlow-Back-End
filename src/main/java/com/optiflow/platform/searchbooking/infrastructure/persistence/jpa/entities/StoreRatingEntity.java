package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "store_ratings")
public class StoreRatingEntity {

  @EmbeddedId
  private PatientStoreKey id;

  @Column(nullable = false, precision = 3, scale = 2)
  private BigDecimal score;

  @Column(length = 280)
  private String comment;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public PatientStoreKey getId() {
    return id;
  }

  public void setId(PatientStoreKey id) {
    this.id = id;
  }

  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
