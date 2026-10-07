package com.optiflow.platform.searchbooking.domain.entities;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import java.time.Instant;

public class PatientStoreRating {

  private final PatientId patientId;
  private final OpticalStoreId opticalStoreId;
  private final StoreRating score;
  private final String comment;
  private final Instant createdAt;

  private PatientStoreRating(
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      StoreRating score,
      String comment,
      Instant createdAt) {
    this.patientId = patientId;
    this.opticalStoreId = opticalStoreId;
    this.score = score;
    this.comment = comment;
    this.createdAt = createdAt;
  }

  public static PatientStoreRating create(
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      StoreRating score,
      String comment,
      Instant createdAt) {
    return new PatientStoreRating(patientId, opticalStoreId, score, comment, createdAt);
  }

  public static PatientStoreRating reconstitute(
      PatientId patientId,
      OpticalStoreId opticalStoreId,
      StoreRating score,
      String comment,
      Instant createdAt) {
    return new PatientStoreRating(patientId, opticalStoreId, score, comment, createdAt);
  }

  public PatientId patientId() {
    return patientId;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public StoreRating score() {
    return score;
  }

  public String comment() {
    return comment;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
