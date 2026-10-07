package com.optiflow.platform.clinical.domain.entities;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Instant;
import java.util.List;

public class MedicalHistory {

  private static final int MAX_ENTRY_LENGTH = 100;
  private static final int MAX_TEXT_LENGTH = 1000;

  private List<String> allergies;
  private List<String> previousConditions;
  private String familyOcularHistory;
  private Instant lastUpdated;

  private MedicalHistory(
      List<String> allergies,
      List<String> previousConditions,
      String familyOcularHistory,
      Instant lastUpdated) {
    this.allergies = allergies;
    this.previousConditions = previousConditions;
    this.familyOcularHistory = familyOcularHistory;
    this.lastUpdated = lastUpdated;
  }

  public static MedicalHistory record(
      List<String> allergies,
      List<String> previousConditions,
      String familyOcularHistory,
      Instant now) {
    MedicalHistory history = new MedicalHistory(List.of(), List.of(), null, now);
    history.update(allergies, previousConditions, familyOcularHistory, now);
    return history;
  }

  public static MedicalHistory reconstitute(
      List<String> allergies,
      List<String> previousConditions,
      String familyOcularHistory,
      Instant lastUpdated) {
    return new MedicalHistory(
        List.copyOf(allergies), List.copyOf(previousConditions), familyOcularHistory, lastUpdated);
  }

  public void update(
      List<String> allergies,
      List<String> previousConditions,
      String familyOcularHistory,
      Instant now) {
    this.allergies = normalizeEntries(allergies, "Allergies");
    this.previousConditions = normalizeEntries(previousConditions, "Previous conditions");
    this.familyOcularHistory = normalizeText(familyOcularHistory);
    this.lastUpdated = now;
  }

  private static List<String> normalizeEntries(List<String> entries, String field) {
    if (entries == null) {
      return List.of();
    }
    List<String> normalized = entries.stream()
        .filter(entry -> entry != null && !entry.isBlank())
        .map(entry -> entry.replaceAll("\\s+", " ").trim())
        .toList();
    if (normalized.stream().anyMatch(entry -> entry.length() > MAX_ENTRY_LENGTH)) {
      throw new DomainException(field + " entries must have at most 100 characters.", 400);
    }
    if (String.join("\n", normalized).length() > MAX_TEXT_LENGTH) {
      throw new DomainException(field + " must have at most 1000 characters in total.", 400);
    }
    return normalized;
  }

  private static String normalizeText(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.length() > MAX_TEXT_LENGTH) {
      throw new DomainException(
          "Family ocular history must have at most 1000 characters.", 400);
    }
    return trimmed;
  }

  public List<String> allergies() {
    return allergies;
  }

  public List<String> previousConditions() {
    return previousConditions;
  }

  public String familyOcularHistory() {
    return familyOcularHistory;
  }

  public Instant lastUpdated() {
    return lastUpdated;
  }
}
