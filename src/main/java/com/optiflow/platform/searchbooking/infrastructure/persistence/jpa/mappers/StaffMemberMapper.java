package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.searchbooking.domain.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.StaffMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class StaffMemberMapper {

  public StaffMemberEntity toEntity(StaffMember staffMember, String passwordHash) {
    StaffMemberEntity entity = new StaffMemberEntity();
    entity.setId(staffMember.id().value());
    entity.setOpticalStoreId(staffMember.opticalStoreId().value());
    entity.setFullName(staffMember.name().value());
    entity.setEmail(staffMember.email().value());
    entity.setPasswordHash(passwordHash);
    entity.setRole(staffMember.role().name());
    entity.setActive(staffMember.active());
    entity.setCreatedAt(staffMember.createdAt());
    return entity;
  }

  public StaffMemberEntity toEntity(StaffMember staffMember, StaffMemberEntity existing) {
    existing.setOpticalStoreId(staffMember.opticalStoreId().value());
    existing.setFullName(staffMember.name().value());
    existing.setEmail(staffMember.email().value());
    existing.setRole(staffMember.role().name());
    existing.setActive(staffMember.active());
    return existing;
  }

  public StaffMember toDomain(StaffMemberEntity entity) {
    return StaffMember.reconstitute(
        StaffMemberId.of(entity.getId()),
        OpticalStoreId.of(entity.getOpticalStoreId()),
        new Name(entity.getFullName()),
        new EmailAddress(entity.getEmail()),
        UserRole.valueOf(entity.getRole()),
        entity.isActive(),
        entity.getCreatedAt());
  }
}
