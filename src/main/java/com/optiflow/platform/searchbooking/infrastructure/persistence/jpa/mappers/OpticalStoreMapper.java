package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreName;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreStatus;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.OpticalStoreEntity;
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
