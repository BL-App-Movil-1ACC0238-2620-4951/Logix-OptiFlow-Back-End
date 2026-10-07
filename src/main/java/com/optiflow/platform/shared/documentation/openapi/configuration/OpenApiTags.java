package com.optiflow.platform.shared.documentation.openapi.configuration;

/**
 * Swagger groups, one per bounded context and resource. {@link OpenApiConfiguration} lists them
 * in the order of the business flow.
 */
public final class OpenApiTags {

  public static final String PATIENTS = "Search & Booking - Patients";
  public static final String OPTICAL_STORES = "Search & Booking - Optical Stores";
  public static final String AVAILABILITY = "Search & Booking - Availability";
  public static final String APPOINTMENTS = "Search & Booking - Appointments";
  public static final String FAVORITES = "Search & Booking - Favorites";
  public static final String RATINGS = "Search & Booking - Ratings";
  public static final String CLINICAL_RECORDS = "Clinical & Commercial - Clinical Records";
  public static final String QUOTATIONS = "Clinical & Commercial - Quotations";
  public static final String SALES = "Clinical & Commercial - Sales";

  private OpenApiTags() {
  }
}
