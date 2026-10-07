package com.optiflow.booking.infrastructure.persistence.mapper;

import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.model.StoreStatus;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PhoneNumber;
import com.optiflow.booking.domain.vo.StoreAddress;
import com.optiflow.booking.domain.vo.StoreName;
import com.optiflow.booking.domain.vo.StoreRating;
import com.optiflow.booking.infrastructure.persistence.entity.OpticalStoreEntity;
import org.springframework.stereotype.Component;

@Component
public class OpticalStoreMapper {

  public OpticalStore toDomain(OpticalStoreEntity entity) {
    return OpticalStore.reconstitute(
        OpticalStoreId.of(entity.getId()),
        new StoreName(entity.getName()),
        new StoreAddress(entity.getAddress()),
        new PhoneNumber(entity.getPhone()),
        StoreRating.of(entity.getRating()),
        StoreStatus.valueOf(entity.getStatus()));
  }

  public OpticalStoreEntity toEntity(OpticalStore store) {
    OpticalStoreEntity entity = new OpticalStoreEntity();
    entity.setId(store.id().value());
    entity.setName(store.name().value());
    entity.setAddress(store.address().value());
    entity.setPhone(store.phone().value());
    entity.setRating(store.rating().value());
    entity.setStatus(store.status().name());
    return entity;
  }
}
