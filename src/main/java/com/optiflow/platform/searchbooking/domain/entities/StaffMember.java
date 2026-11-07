package com.optiflow.platform.searchbooking.domain.entities;

import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;

public class StaffMember {

  private final StaffMemberId id;
  private final OpticalStoreId opticalStoreId;
  private final Name name;
  private final EmailAddress email;
  private UserRole role;
  private boolean active;
  private final Instant createdAt;

  private StaffMember(
      StaffMemberId id,
      OpticalStoreId opticalStoreId,
      Name name,
      EmailAddress email,
      UserRole role,
      boolean active,
      Instant createdAt) {
    this.id = id;
    this.opticalStoreId = opticalStoreId;
    this.name = name;
    this.email = email;
    this.role = role;
    this.active = active;
    this.createdAt = createdAt;
  }

  public static StaffMember register(
      StaffMemberId id,
      OpticalStoreId opticalStoreId,
      Name name,
      EmailAddress email,
      UserRole role,
      Instant createdAt) {
    if (role == UserRole.PATIENT) {
      throw new DomainException("Staff members cannot have the PATIENT role.", 400);
    }
    return new StaffMember(id, opticalStoreId, name, email, role, true, createdAt);
  }

  public static StaffMember reconstitute(
      StaffMemberId id,
      OpticalStoreId opticalStoreId,
      Name name,
      EmailAddress email,
      UserRole role,
      boolean active,
      Instant createdAt) {
    return new StaffMember(id, opticalStoreId, name, email, role, active, createdAt);
  }

  public void assignRole(UserRole newRole) {
    if (newRole == UserRole.PATIENT) {
      throw new DomainException("Staff members cannot have the PATIENT role.", 400);
    }
    this.role = newRole;
  }

  public StaffMemberId id() {
    return id;
  }

  public OpticalStoreId opticalStoreId() {
    return opticalStoreId;
  }

  public Name name() {
    return name;
  }

  public EmailAddress email() {
    return email;
  }

  public UserRole role() {
    return role;
  }

  public boolean active() {
    return active;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
