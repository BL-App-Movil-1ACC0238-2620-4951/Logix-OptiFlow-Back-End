package com.optiflow.platform.searchbooking.domain.model.entities;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public class Patient {

  private final PatientId id;
  private final Name name;
  private final EmailAddress email;
  private final PhoneNumber phone;
  private final LocalDate birthDate;
  private final Instant createdAt;

  private Patient(
      PatientId id,
      Name name,
      EmailAddress email,
      PhoneNumber phone,
      LocalDate birthDate,
      Instant createdAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.birthDate = birthDate;
    this.createdAt = createdAt;
  }

  public static Patient register(
      PatientId id,
      Name name,
      EmailAddress email,
      PhoneNumber phone,
      LocalDate birthDate,
      Instant createdAt) {
    return new Patient(id, name, email, phone, birthDate, createdAt);
  }

  public static Patient reconstitute(
      PatientId id,
      Name name,
      EmailAddress email,
      PhoneNumber phone,
      LocalDate birthDate,
      Instant createdAt) {
    return new Patient(id, name, email, phone, birthDate, createdAt);
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

  public Optional<LocalDate> birthDate() {
    return Optional.ofNullable(birthDate);
  }

  public Instant createdAt() {
    return createdAt;
  }
}
