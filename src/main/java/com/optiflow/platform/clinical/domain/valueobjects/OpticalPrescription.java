package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.math.BigDecimal;

public record OpticalPrescription(
    BigDecimal sphereOd,
    BigDecimal sphereOs,
    BigDecimal cylinderOd,
    BigDecimal cylinderOs,
    Integer axisOd,
    Integer axisOs,
    BigDecimal addition,
    String treatment,
    String recommendedFrameType) {

  private static final BigDecimal MAX_SPHERE = new BigDecimal("30.00");
  private static final BigDecimal MAX_CYLINDER = new BigDecimal("10.00");
  private static final BigDecimal MAX_ADDITION = new BigDecimal("4.00");
  private static final int MAX_AXIS = 180;
  private static final int MAX_TEXT_LENGTH = 255;

  public OpticalPrescription {
    requireRange(sphereOd, MAX_SPHERE.negate(), MAX_SPHERE, "Sphere OD");
    requireRange(sphereOs, MAX_SPHERE.negate(), MAX_SPHERE, "Sphere OS");
    requireRange(cylinderOd, MAX_CYLINDER.negate(), MAX_CYLINDER, "Cylinder OD");
    requireRange(cylinderOs, MAX_CYLINDER.negate(), MAX_CYLINDER, "Cylinder OS");
    requireAxis(axisOd, "Axis OD");
    requireAxis(axisOs, "Axis OS");
    if (addition != null) {
      requireRange(addition, BigDecimal.ZERO, MAX_ADDITION, "Addition");
    }
    treatment = normalize(treatment, "Treatment");
    recommendedFrameType = normalize(recommendedFrameType, "Recommended frame type");
  }

  public boolean isValid() {
    return inRange(sphereOd, MAX_SPHERE.negate(), MAX_SPHERE)
        && inRange(sphereOs, MAX_SPHERE.negate(), MAX_SPHERE)
        && inRange(cylinderOd, MAX_CYLINDER.negate(), MAX_CYLINDER)
        && inRange(cylinderOs, MAX_CYLINDER.negate(), MAX_CYLINDER)
        && axisOd != null && axisOd >= 0 && axisOd <= MAX_AXIS
        && axisOs != null && axisOs >= 0 && axisOs <= MAX_AXIS
        && (addition == null || inRange(addition, BigDecimal.ZERO, MAX_ADDITION));
  }

  private static boolean inRange(BigDecimal value, BigDecimal min, BigDecimal max) {
    return value != null && value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
  }

  private static void requireRange(BigDecimal value, BigDecimal min, BigDecimal max, String field) {
    if (value == null) {
      throw new DomainException(field + " is required.", 400);
    }
    if (!inRange(value, min, max)) {
      throw new DomainException(
          field + " must be between " + min.toPlainString() + " and " + max.toPlainString() + ".",
          400);
    }
  }

  private static void requireAxis(Integer value, String field) {
    if (value == null) {
      throw new DomainException(field + " is required.", 400);
    }
    if (value < 0 || value > MAX_AXIS) {
      throw new DomainException(field + " must be between 0 and 180 degrees.", 400);
    }
  }

  private static String normalize(String value, String field) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String trimmed = value.trim();
    if (trimmed.length() > MAX_TEXT_LENGTH) {
      throw new DomainException(field + " must have at most 255 characters.", 400);
    }
    return trimmed;
  }
}
