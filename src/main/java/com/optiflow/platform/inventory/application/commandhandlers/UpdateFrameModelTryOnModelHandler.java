package com.optiflow.platform.inventory.application.commandhandlers;

import com.optiflow.platform.inventory.domain.exceptions.FrameModelNotFoundException;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelTryOnModelCommand;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateFrameModelTryOnModelHandler {

  private final FrameModelRepository frameModelRepository;

  public UpdateFrameModelTryOnModelHandler(FrameModelRepository frameModelRepository) {
    this.frameModelRepository = frameModelRepository;
  }

  @Transactional
  public FrameModel handle(UpdateFrameModelTryOnModelCommand command) {
    FrameModel frameModel = frameModelRepository.findBySku(command.sku())
        .orElseThrow(FrameModelNotFoundException::new);
    frameModel.updateTryOnModel(command.tryOnModelUrl());
    frameModelRepository.save(frameModel);
    return frameModel;
  }
}
