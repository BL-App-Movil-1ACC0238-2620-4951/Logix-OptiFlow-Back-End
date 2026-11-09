package com.optiflow.platform.inventory.application.results;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.entities.Stock;

/** Stock of a frame model together with the frame data that the screens show. */
public record StockLevel(FrameModel frameModel, Stock stock) {
}
