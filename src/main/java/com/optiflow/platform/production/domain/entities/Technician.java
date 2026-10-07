package com.optiflow.platform.production.domain.entities;

import com.optiflow.platform.production.domain.valueobjects.TechnicianId;
import com.optiflow.platform.shared.exceptions.DomainException;

/** Laboratory technician who manufactures the lenses of a work order. */
public class Technician {

  private final TechnicianId id;
  private final String name;

  public Technician(TechnicianId id, String name) {
    if (id == null) {
      throw new DomainException("Technician id is required.", 400);
    }
    if (name == null || name.isBlank() || name.trim().length() > 120) {
      throw new DomainException("Technician name is required (max. 120 characters).", 400);
    }
    this.id = id;
    this.name = name.trim();
  }

  public TechnicianId id() {
    return id;
  }

  public String name() {
    return name;
  }
}
