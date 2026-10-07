package com.optiflow.booking.domain.model;

import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.Name;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.PhoneNumber;
import java.time.Instant;

public class Patient {

  private final PatientId id;
  private final Name name;
  private final EmailAddress email;
  private final PhoneNumber phone;
  private final Instant createdAt;

  private Patient(
      PatientId id, Name name, EmailAddress email, PhoneNumber phone, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.createdAt = createdAt;
  }

  public static Patient register(
      PatientId id, Name name, EmailAddress email, PhoneNumber phone, Instant createdAt) {
    return new Patient(id, name, email, phone, createdAt);
  }

  public static Patient reconstitute(
      PatientId id, Name name, EmailAddress email, PhoneNumber phone, Instant createdAt) {
    return new Patient(id, name, email, phone, createdAt);
  }

  public PatientId id() {
    return id;
  }

  public Name name() {
    return name;
  }

  public EmailAddress email() {
    return email;
  }

  public PhoneNumber phone() {
    return phone;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
