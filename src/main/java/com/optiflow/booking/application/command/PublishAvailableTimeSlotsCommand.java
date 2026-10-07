package com.optiflow.booking.application.command;

import com.optiflow.booking.domain.vo.OpticalStoreId;
import java.time.Instant;
import java.util.List;

public record PublishAvailableTimeSlotsCommand(
    OpticalStoreId opticalStoreId, List<SlotDraft> slots) {

  public record SlotDraft(Instant startDateTime, Instant endDateTime) {
  }
}
