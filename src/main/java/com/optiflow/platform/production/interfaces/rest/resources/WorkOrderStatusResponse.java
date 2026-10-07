package com.optiflow.platform.production.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record WorkOrderStatusResponse(UUID id, String status, Instant updatedAt) {
}
