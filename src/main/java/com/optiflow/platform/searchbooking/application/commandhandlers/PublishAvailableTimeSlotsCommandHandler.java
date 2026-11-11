package com.optiflow.platform.searchbooking.application.commandhandlers;

import com.optiflow.platform.searchbooking.domain.model.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.model.events.OpticalStoresPublished;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.TimeSlotId;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishAvailableTimeSlotsCommandHandler {

  private final OpticalStoreRepository opticalStoreRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public PublishAvailableTimeSlotsCommandHandler(
      OpticalStoreRepository opticalStoreRepository,
      TimeSlotRepository timeSlotRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.timeSlotRepository = timeSlotRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public List<TimeSlot> handle(PublishAvailableTimeSlotsCommand command) {
    opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    List<TimeSlot> published = new ArrayList<>();
    for (PublishAvailableTimeSlotsCommand.SlotDraft draft : command.slots()) {
      boolean overlapsDraft = published.stream().anyMatch(slot ->
          slot.startDateTime().isBefore(draft.endDateTime())
              && slot.endDateTime().isAfter(draft.startDateTime()));
      if (overlapsDraft || timeSlotRepository.existsOverlapping(
          command.opticalStoreId(), draft.startDateTime(), draft.endDateTime())) {
        throw new DomainException("Time slots cannot overlap.", 409);
      }
      TimeSlot timeSlot = TimeSlot.publish(
          TimeSlotId.generate(),
          command.opticalStoreId(),
          draft.startDateTime(),
          draft.endDateTime());
      timeSlotRepository.save(timeSlot);
      published.add(timeSlot);
    }
    eventPublisher.publish(new OpticalStoresPublished(command.opticalStoreId(), clock.instant()));
    return published;
  }
}
