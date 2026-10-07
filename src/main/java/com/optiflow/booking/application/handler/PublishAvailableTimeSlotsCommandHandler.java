package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.PublishAvailableTimeSlotsCommand;
import com.optiflow.booking.domain.event.OpticalStoresPublished;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
import com.optiflow.booking.domain.repository.TimeSlotRepository;
import com.optiflow.booking.domain.vo.TimeSlotId;
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
