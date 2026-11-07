package com.optiflow.platform.searchbooking.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GeoLocationTest {

  @Test
  void computesDistanceBetweenTwoPointsInLima() {
    GeoLocation miraflores = new GeoLocation(new BigDecimal("-12.121037"), new BigDecimal("-77.028240"));
    GeoLocation sanIsidro = new GeoLocation(new BigDecimal("-12.099000"), new BigDecimal("-77.034000"));

    double distanceKm = miraflores.distanceKmTo(sanIsidro);

    assertTrue(distanceKm > 2.0 && distanceKm < 3.5);
  }

  @Test
  void distanceToSamePointIsZero() {
    GeoLocation point = new GeoLocation(new BigDecimal("-12.100000"), new BigDecimal("-77.030000"));
    assertEquals(0.0, point.distanceKmTo(point));
  }
}
