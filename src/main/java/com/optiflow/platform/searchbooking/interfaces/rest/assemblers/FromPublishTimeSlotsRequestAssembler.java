package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.PublishTimeSlotsRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromPublishTimeSlotsRequestAssembler {

  public PublishAvailableTimeSlotsCommand toCommand(UUID opticalStoreId, PublishTimeSlotsRequest request) {
    return new PublishAvailableTimeSlotsCommand(
        OpticalStoreId.of(opticalStoreId),
        request.timeSlots().stream()
            .map(slot -> new PublishAvailableTimeSlotsCommand.SlotDraft(
                slot.startDateTime(), slot.endDateTime()))
            .toList());
  }
}
