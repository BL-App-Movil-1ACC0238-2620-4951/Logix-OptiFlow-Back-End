package com.optiflow.platform.production.domain.valueobjects;

import java.time.Instant;

/** One entry of the status history of a work order. */
public record StatusChange(WorkOrderStatus status, Instant changedAt) {
}
