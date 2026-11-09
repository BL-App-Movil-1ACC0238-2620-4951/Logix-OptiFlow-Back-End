package com.optiflow.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.domain.repositories.FrameModelRepository;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers.FrameModelMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class FrameModelRepositoryImpl implements FrameModelRepository {

  private final FrameModelJpaRepository frameModelJpaRepository;
  private final FrameModelMapper frameModelMapper;

  public FrameModelRepositoryImpl(
      FrameModelJpaRepository frameModelJpaRepository, FrameModelMapper frameModelMapper) {
    this.frameModelJpaRepository = frameModelJpaRepository;
    this.frameModelMapper = frameModelMapper;
  }

  @Override
  public void save(FrameModel frameModel) {
    frameModelJpaRepository.save(frameModelMapper.toEntity(frameModel));
  }

  @Override
  public Optional<FrameModel> findById(FrameModelId id) {
    return frameModelJpaRepository.findById(id.value()).map(frameModelMapper::toDomain);
  }

  @Override
  public Optional<FrameModel> findBySku(Sku sku) {
    return frameModelJpaRepository.findBySku(sku.value()).map(frameModelMapper::toDomain);
  }

  @Override
  public List<FrameModel> findAll() {
    return frameModelJpaRepository.findAllByOrderByName().stream()
        .map(frameModelMapper::toDomain)
        .toList();
  }
}
