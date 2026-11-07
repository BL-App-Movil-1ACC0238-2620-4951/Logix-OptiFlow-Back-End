package com.optiflow.platform.shared.interfaces.rest;

/** Base path for versioned public HTTP resources (Cap. 4 REST conventions). */
public final class ApiV1 {

  public static final String BASE_PATH = "/api/v1";

  /** Maps a resource path (with leading slash) under the versioned API. */
  public static String path(String resourcePath) {
    if (resourcePath == null || resourcePath.isEmpty()) {
      return BASE_PATH;
    }
    return resourcePath.startsWith("/") ? BASE_PATH + resourcePath : BASE_PATH + "/" + resourcePath;
  }

  private ApiV1() {}
}
