package com.optiflow.booking.domain.model;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import java.time.Instant;

public class FavoriteStore {

  private final PatientId patientId;
  private final OpticalStoreId opticalStoreId;
  private final Instant savedAt;

  private FavoriteStore(PatientId patientId, OpticalStoreId opticalStoreId, Instant savedAt) {
    this.patientId = patientId;
    this.opticalStoreId = opticalStoreId;
    this.savedAt = savedAt;
  }

  public static FavoriteStore save(
      PatientId patientId, OpticalStoreId opticalStoreId, Instant savedAt) {
    return new FavoriteStore(patientId, opticalStoreId, savedAt);
  }

  public static FavoriteStore reconstitute(
      PatientId patientId, OpticalStoreId opticalStoreId, Instant savedAt) {
    return new FavoriteStore(patientId, opticalStoreId, savedAt);
  }

  public PatientId patientId() {
    return patientId;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public Instant savedAt() {
    return savedAt;
  }
}
