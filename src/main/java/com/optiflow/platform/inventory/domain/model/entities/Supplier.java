package com.optiflow.platform.inventory.domain.model.entities;

import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Supplier that delivers the frames of the replenishments. */
public class Supplier {

  private final SupplierId id;
  private final String name;
  private final String contact;

  public Supplier(SupplierId id, String name, String contact) {
    if (id == null) {
      throw new DomainException("Supplier id is required.", 400);
    }
    if (name == null || name.isBlank() || name.trim().length() > 255) {
      throw new DomainException("Supplier name is required (max. 255 characters).", 400);
    }
    if (contact != null && contact.trim().length() > 255) {
      throw new DomainException("Supplier contact must have at most 255 characters.", 400);
    }
    this.id = id;
    this.name = name.trim();
    this.contact = contact == null || contact.isBlank() ? null : contact.trim();
  }

  public static Supplier register(String name, String contact) {
    return new Supplier(SupplierId.generate(), name, contact);
  }

  public SupplierId id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String contact() {
    return contact;
  }
}
