package com.optiflow.platform.production.domain.entities;

import com.optiflow.platform.production.domain.valueobjects.LensId;
import com.optiflow.platform.shared.exceptions.DomainException;

/** A lens to be manufactured according to the technical specifications of the order. */
public class Lenses {

  private static final int MAX_SPECIFICATIONS_LENGTH = 500;

  private final LensId id;
  private final String specifications;
  private boolean completed;

  private Lenses(LensId id, String specifications, boolean completed) {
    this.id = id;
    this.specifications = specifications;
    this.completed = completed;
  }

  public static Lenses create(String specifications) {
    if (specifications == null || specifications.isBlank()) {
      throw new DomainException("Lens specifications are required.", 400);
    }
    if (specifications.trim().length() > MAX_SPECIFICATIONS_LENGTH) {
      throw new DomainException("Lens specifications must have at most 500 characters.", 400);
    }
    return new Lenses(LensId.generate(), specifications.trim(), false);
  }

  public static Lenses reconstitute(LensId id, String specifications, boolean completed) {
    return new Lenses(id, specifications, completed);
  }

  public void complete() {
    completed = true;
  }

  public LensId id() {
    return id;
  }

  public String specifications() {
    return specifications;
  }

  public boolean completed() {
    return completed;
  }
}
