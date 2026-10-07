package com.optiflow.platform.notification.domain.entities;

import com.optiflow.platform.notification.domain.valueobjects.LoyaltyPoints;
import com.optiflow.platform.notification.domain.valueobjects.PatientId;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.Optional;

public class LoyaltyAccount {

  public static final LoyaltyPoints BIRTHDAY_BONUS_POINTS = LoyaltyPoints.of(100);

  private final PatientId patientId;
  private LoyaltyPoints balance;
  private Integer lastBirthdayDiscountYear;
  private Instant updatedAt;

  private LoyaltyAccount(
      PatientId patientId,
      LoyaltyPoints balance,
      Integer lastBirthdayDiscountYear,
      Instant updatedAt) {
    this.patientId = patientId;
    this.balance = balance;
    this.lastBirthdayDiscountYear = lastBirthdayDiscountYear;
    this.updatedAt = updatedAt;
  }

  public static LoyaltyAccount open(PatientId patientId, Instant now) {
    return new LoyaltyAccount(patientId, LoyaltyPoints.ZERO, null, now);
  }

  public static LoyaltyAccount reconstitute(
      PatientId patientId,
      LoyaltyPoints balance,
      Integer lastBirthdayDiscountYear,
      Instant updatedAt) {
    return new LoyaltyAccount(patientId, balance, lastBirthdayDiscountYear, updatedAt);
  }

  public LoyaltyPoints grantBirthdayDiscount(int calendarYear, Instant now) {
    if (lastBirthdayDiscountYear != null && lastBirthdayDiscountYear == calendarYear) {
      throw new DomainException("Birthday discount was already granted for this year.", 409);
    }
    lastBirthdayDiscountYear = calendarYear;
    balance = balance.add(BIRTHDAY_BONUS_POINTS);
    updatedAt = now;
    return BIRTHDAY_BONUS_POINTS;
  }

  public void earn(LoyaltyPoints points, Instant now) {
    balance = balance.add(points);
    updatedAt = now;
  }

  public PatientId patientId() {
    return patientId;
  }

  public LoyaltyPoints balance() {
    return balance;
  }

  public Optional<Integer> lastBirthdayDiscountYear() {
    return Optional.ofNullable(lastBirthdayDiscountYear);
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
