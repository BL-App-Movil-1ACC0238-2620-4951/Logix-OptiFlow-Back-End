package com.optiflow.platform.inventory.application.queryservices;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.queries.GetFrameCatalogQuery;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FrameCatalogQueryService {

  private final FrameModelRepository frameModelRepository;

  public FrameCatalogQueryService(FrameModelRepository frameModelRepository) {
    this.frameModelRepository = frameModelRepository;
  }

  @Transactional(readOnly = true)
  public List<FrameModel> handle(GetFrameCatalogQuery query) {
    return frameModelRepository.findAll();
  }
}
