package com.optiflow.platform.inventory.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.inventory.domain.model.entities.Supplier;
import com.optiflow.platform.inventory.domain.model.valueobjects.SupplierId;
import com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

  public Supplier toDomain(SupplierEntity entity) {
    return new Supplier(SupplierId.of(entity.getId()), entity.getName(), entity.getContact());
  }

  public SupplierEntity toEntity(Supplier supplier) {
    SupplierEntity entity = new SupplierEntity();
    entity.setId(supplier.id().value());
    entity.setName(supplier.name());
    entity.setContact(supplier.contact());
    return entity;
  }
}
