package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.UUID;

public record ClinicalRecordId(UUID value) {

  public ClinicalRecordId {
    if (value == null) {
      throw new DomainException("Clinical record id is required.", 400);
    }
  }

  public static ClinicalRecordId generate() {
    return new ClinicalRecordId(UUID.randomUUID());
  }

  public static ClinicalRecordId of(UUID value) {
    return new ClinicalRecordId(value);
  }
}
