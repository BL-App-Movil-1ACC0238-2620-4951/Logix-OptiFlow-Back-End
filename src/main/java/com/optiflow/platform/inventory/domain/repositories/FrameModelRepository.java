package com.optiflow.platform.inventory.domain.repositories;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import java.util.List;
import java.util.Optional;

public interface FrameModelRepository {

  void save(FrameModel frameModel);

  Optional<FrameModel> findById(FrameModelId id);

  Optional<FrameModel> findBySku(Sku sku);

  List<FrameModel> findAll();
}
