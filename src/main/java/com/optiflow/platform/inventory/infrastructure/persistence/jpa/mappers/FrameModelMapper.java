package com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.inventory.domain.model.aggregates.FrameModel;
import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.FrameModelEntity;
import org.springframework.stereotype.Component;

@Component
public class FrameModelMapper {

  public FrameModel toDomain(FrameModelEntity entity) {
    return FrameModel.reconstitute(
        FrameModelId.of(entity.getId()),
        Sku.of(entity.getSku()),
        entity.getName(),
        entity.getMaterial(),
        entity.getShape(),
        entity.getColor(),
        new Price(entity.getPriceAmount(), entity.getPriceCurrency()),
        entity.getTryOnModelUrl());
  }

  public FrameModelEntity toEntity(FrameModel frameModel) {
    FrameModelEntity entity = new FrameModelEntity();
    entity.setId(frameModel.id().value());
    entity.setSku(frameModel.sku().value());
    entity.setName(frameModel.name());
    entity.setMaterial(frameModel.material());
    entity.setShape(frameModel.shape());
    entity.setColor(frameModel.color());
    entity.setPriceAmount(frameModel.price().amount());
    entity.setPriceCurrency(frameModel.price().currency());
    entity.setTryOnModelUrl(frameModel.tryOnModelUrl());
    return entity;
  }
}
