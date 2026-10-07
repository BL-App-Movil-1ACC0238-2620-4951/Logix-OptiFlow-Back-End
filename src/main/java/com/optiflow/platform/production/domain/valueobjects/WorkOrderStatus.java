package com.optiflow.platform.production.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/** Kanban stages of a work order, in the order they must be followed. */
public enum WorkOrderStatus {
  PENDING, IN_WORKSHOP, QUALITY_CONTROL, READY_FOR_DELIVERY, DELIVERED;

  public static WorkOrderStatus from(String value) {
    return Arrays.stream(values())
        .filter(candidate -> candidate.name().equalsIgnoreCase(value == null ? "" : value.trim()))
        .findFirst()
        .orElseThrow(() -> new DomainException(
            "Work order status must be one of: "
                + Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "))
                + ".",
            400));
  }

  public Optional<WorkOrderStatus> next() {
    return ordinal() + 1 < values().length
        ? Optional.of(values()[ordinal() + 1])
        : Optional.empty();
  }
}
