package com.optiflow.platform.inventory.application.results;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.entities.Stock;

/** @param stock stock in the requested optical store; {@code null} when none was requested or held */
public record FrameModelDetails(FrameModel frameModel, Stock stock) {
}
