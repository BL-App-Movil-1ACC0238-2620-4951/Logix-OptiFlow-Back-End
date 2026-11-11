package com.optiflow.platform.inventory.application.services;

import com.optiflow.platform.inventory.application.commandhandlers.AddNewFrameModelHandler;
import com.optiflow.platform.inventory.application.commandhandlers.UpdateFrameModelPriceHandler;
import com.optiflow.platform.inventory.application.commandhandlers.UpdateFrameModelTryOnModelHandler;
import com.optiflow.platform.inventory.application.queryservices.FrameCatalogQueryService;
import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.commands.AddNewFrameModelCommand;
import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelPriceCommand;
import com.optiflow.platform.inventory.domain.model.commands.UpdateFrameModelTryOnModelCommand;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameCatalogQuery;
import java.util.List;
import org.springframework.stereotype.Service;

/** Facade of the frame catalog and price use cases. */
@Service
public class FrameModelApplicationService {

  private final AddNewFrameModelHandler addNewFrameModelHandler;
  private final UpdateFrameModelPriceHandler updateFrameModelPriceHandler;
  private final UpdateFrameModelTryOnModelHandler updateFrameModelTryOnModelHandler;
  private final FrameCatalogQueryService frameCatalogQueryService;

  public FrameModelApplicationService(
      AddNewFrameModelHandler addNewFrameModelHandler,
      UpdateFrameModelPriceHandler updateFrameModelPriceHandler,
      UpdateFrameModelTryOnModelHandler updateFrameModelTryOnModelHandler,
      FrameCatalogQueryService frameCatalogQueryService) {
    this.addNewFrameModelHandler = addNewFrameModelHandler;
    this.updateFrameModelPriceHandler = updateFrameModelPriceHandler;
    this.updateFrameModelTryOnModelHandler = updateFrameModelTryOnModelHandler;
    this.frameCatalogQueryService = frameCatalogQueryService;
  }

  public FrameModel add(AddNewFrameModelCommand command) {
    return addNewFrameModelHandler.handle(command);
  }

  public FrameModel updatePrice(UpdateFrameModelPriceCommand command) {
    return updateFrameModelPriceHandler.handle(command);
  }

  public FrameModel updateTryOnModel(UpdateFrameModelTryOnModelCommand command) {
    return updateFrameModelTryOnModelHandler.handle(command);
  }

  public List<FrameModel> catalog(GetFrameCatalogQuery query) {
    return frameCatalogQueryService.handle(query);
  }
}
