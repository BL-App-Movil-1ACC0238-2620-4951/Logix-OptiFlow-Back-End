package com.optiflow.platform.notification.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccountEntity {

  @Id
  @Column(name = "patient_id")
  private UUID patientId;

  @Column(nullable = false)
  private int balance;

  @Column(name = "last_birthday_discount_year")
  private Integer lastBirthdayDiscountYear;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public UUID getPatientId() {
    return patientId;
  }

  public void setPatientId(UUID patientId) {
    this.patientId = patientId;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public Integer getLastBirthdayDiscountYear() {
    return lastBirthdayDiscountYear;
  }

  public void setLastBirthdayDiscountYear(Integer lastBirthdayDiscountYear) {
    this.lastBirthdayDiscountYear = lastBirthdayDiscountYear;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
