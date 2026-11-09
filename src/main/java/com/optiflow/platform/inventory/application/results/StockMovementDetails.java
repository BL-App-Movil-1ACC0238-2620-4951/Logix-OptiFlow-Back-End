package com.optiflow.platform.inventory.application.results;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.entities.StockMovement;

public record StockMovementDetails(StockMovement movement, FrameModel frameModel) {
}
