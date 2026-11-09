package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.domain.exceptions.FrameModelNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelPriceCommand;
import com.optiflow.platform.inventory.domain.model.events.FrameModelPriceUpdated;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateFrameModelPriceHandler {

  private final FrameModelRepository frameModelRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public UpdateFrameModelPriceHandler(
      FrameModelRepository frameModelRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.frameModelRepository = frameModelRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public FrameModel handle(UpdateFrameModelPriceCommand command) {
    FrameModel frameModel = frameModelRepository.findBySku(command.sku())
        .orElseThrow(FrameModelNotFoundException::new);
    frameModel.updatePrice(command.price());
    frameModelRepository.save(frameModel);
    eventPublisher.publish(
        new FrameModelPriceUpdated(frameModel.id(), frameModel.price(), clock.instant()));
    return frameModel;
  }
}
