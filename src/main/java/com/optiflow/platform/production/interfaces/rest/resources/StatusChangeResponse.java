package com.optiflow.platform.production.interfaces.rest.resources;

import java.time.Instant;

public record StatusChangeResponse(String status, Instant changedAt) {
}
