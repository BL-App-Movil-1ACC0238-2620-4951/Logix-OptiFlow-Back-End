package com.optiflow.platform.shared.interfaces.rest;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Builds {@code 201 Created} responses with a {@code Location} header. */
public final class CreatedResourceResponses {

  public static <T> ResponseEntity<T> at(String pathTemplate, T body, Object... uriVariables) {
    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path(ApiV1.path(pathTemplate))
        .buildAndExpand(uriVariables)
        .toUri();
    return ResponseEntity.created(location).body(body);
  }

  private CreatedResourceResponses() {}
}
