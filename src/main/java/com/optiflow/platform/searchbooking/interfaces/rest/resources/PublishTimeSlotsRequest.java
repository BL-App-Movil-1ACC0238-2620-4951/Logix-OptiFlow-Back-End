package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record PublishTimeSlotsRequest(@NotEmpty @Valid List<SlotRequest> timeSlots) {

  public record SlotRequest(
      @NotNull Instant startDateTime, @NotNull Instant endDateTime) {}
}
