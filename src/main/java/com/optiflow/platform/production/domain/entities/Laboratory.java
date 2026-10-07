package com.optiflow.platform.production.domain.entities;

import com.optiflow.platform.production.domain.valueobjects.LaboratoryId;
import com.optiflow.platform.shared.exceptions.DomainException;

/** Laboratory where the lenses and frames of a work order are manufactured. */
public class Laboratory {

  private final LaboratoryId id;
  private final String name;

  public Laboratory(LaboratoryId id, String name) {
    if (id == null) {
      throw new DomainException("Laboratory id is required.", 400);
    }
    if (name == null || name.isBlank() || name.trim().length() > 120) {
      throw new DomainException("Laboratory name is required (max. 120 characters).", 400);
    }
    this.id = id;
    this.name = name.trim();
  }

  public LaboratoryId id() {
    return id;
  }

  public String name() {
    return name;
  }
}
