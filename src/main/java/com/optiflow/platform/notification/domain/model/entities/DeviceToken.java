package com.optiflow.platform.notification.domain.model.entities;

import com.optiflow.platform.notification.domain.model.valueobjects.DevicePlatform;
import com.optiflow.platform.notification.domain.model.valueobjects.DeviceTokenId;
import com.optiflow.platform.notification.domain.model.valueobjects.PatientId;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;

/** Firebase Cloud Messaging registration token of a device of the patient. */
public class DeviceToken {

  private static final int MAX_TOKEN_LENGTH = 512;

  private final DeviceTokenId id;
  private PatientId patientId;
  private final String token;
  private DevicePlatform platform;
  private Instant updatedAt;

  private DeviceToken(
      DeviceTokenId id,
      PatientId patientId,
      String token,
      DevicePlatform platform,
      Instant updatedAt) {
    this.id = id;
    this.patientId = patientId;
    this.token = token;
    this.platform = platform;
    this.updatedAt = updatedAt;
  }

  public static DeviceToken register(
      PatientId patientId, String token, DevicePlatform platform, Instant now) {
    if (token == null || token.isBlank()) {
      throw new DomainException("Device token is required.", 400);
    }
    if (token.length() > MAX_TOKEN_LENGTH) {
      throw new DomainException("Device token must have at most 512 characters.", 400);
    }
    return new DeviceToken(DeviceTokenId.generate(), patientId, token, platform, now);
  }

  public static DeviceToken reconstitute(
      DeviceTokenId id,
      PatientId patientId,
      String token,
      DevicePlatform platform,
      Instant updatedAt) {
    return new DeviceToken(id, patientId, token, platform, updatedAt);
  }

  /** A device that is registered again belongs to the patient who signed in last. */
  public void renew(PatientId patientId, DevicePlatform platform, Instant now) {
    this.patientId = patientId;
    this.platform = platform;
    this.updatedAt = now;
  }

  public DeviceTokenId id() {
    return id;
  }

  public PatientId patientId() {
    return patientId;
  }

  public String token() {
    return token;
  }

  public DevicePlatform platform() {
    return platform;
  }

  public Instant updatedAt() {
    return updatedAt;
  }
}
