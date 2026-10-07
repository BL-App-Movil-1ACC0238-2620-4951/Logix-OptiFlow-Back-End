package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import java.time.Instant;
import java.util.List;

public record PublishAvailableTimeSlotsCommand(
    OpticalStoreId opticalStoreId, List<SlotDraft> slots) {

  public record SlotDraft(Instant startDateTime, Instant endDateTime) {
  }
}
