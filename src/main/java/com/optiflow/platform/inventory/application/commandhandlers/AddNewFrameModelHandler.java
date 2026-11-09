package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.commands.AddNewFrameModelCommand;
import com.optiflow.platform.inventory.domain.model.events.NewFrameModelAdded;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddNewFrameModelHandler {

  private final FrameModelRepository frameModelRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public AddNewFrameModelHandler(
      FrameModelRepository frameModelRepository, DomainEventPublisher eventPublisher, Clock clock) {
    this.frameModelRepository = frameModelRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public FrameModel handle(AddNewFrameModelCommand command) {
    if (frameModelRepository.findBySku(command.sku()).isPresent()) {
      throw new DomainException("A frame model with this SKU already exists.", 409);
    }
    FrameModel frameModel = FrameModel.add(command.sku(), command.name(), command.material(),
        command.shape(), command.color(), command.price(), command.tryOnModelUrl());
    frameModelRepository.save(frameModel);
    eventPublisher.publish(
        new NewFrameModelAdded(frameModel.id(), frameModel.sku(), clock.instant()));
    return frameModel;
  }
}
