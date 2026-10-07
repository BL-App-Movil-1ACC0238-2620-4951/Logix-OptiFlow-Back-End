package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.events.OpticalStoresPublished;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.searchbooking.domain.valueobjects.TimeSlotId;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
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
  public void handle(PublishAvailableTimeSlotsCommand command) {
    opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    for (PublishAvailableTimeSlotsCommand.SlotDraft draft : command.slots()) {
      TimeSlot timeSlot = TimeSlot.publish(
          TimeSlotId.generate(),
          command.opticalStoreId(),
          draft.startDateTime(),
          draft.endDateTime());
      timeSlotRepository.save(timeSlot);
    }
    eventPublisher.publish(new OpticalStoresPublished(command.opticalStoreId(), clock.instant()));
  }
}
