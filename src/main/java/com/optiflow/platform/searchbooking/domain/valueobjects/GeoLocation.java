package com.optiflow.platform.searchbooking.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record GeoLocation(BigDecimal latitude, BigDecimal longitude) {

  private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90");
  private static final BigDecimal MAX_LATITUDE = new BigDecimal("90");
  private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180");
  private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180");
  private static final double EARTH_RADIUS_KM = 6371.0;

  public GeoLocation {
    if (latitude == null || longitude == null) {
      throw new DomainException("Latitude and longitude are required.", 400);
    }
    if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
      throw new DomainException("Latitude must be between -90 and 90.", 400);
    }
    if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
      throw new DomainException("Longitude must be between -180 and 180.", 400);
    }
  }

  public double distanceKmTo(GeoLocation other) {
    double lat1 = toRadians(latitude);
    double lat2 = toRadians(other.latitude);
    double deltaLat = toRadians(other.latitude.subtract(latitude));
    double deltaLng = toRadians(other.longitude.subtract(longitude));

    double haversine = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
        + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
    double centralAngle = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    return EARTH_RADIUS_KM * centralAngle;
  }

  private static double toRadians(BigDecimal degrees) {
    return Math.toRadians(degrees.doubleValue());
  }

  public BigDecimal latitudeScaled() {
    return latitude.setScale(6, RoundingMode.HALF_UP);
  }

  public BigDecimal longitudeScaled() {
    return longitude.setScale(6, RoundingMode.HALF_UP);
  }
}
