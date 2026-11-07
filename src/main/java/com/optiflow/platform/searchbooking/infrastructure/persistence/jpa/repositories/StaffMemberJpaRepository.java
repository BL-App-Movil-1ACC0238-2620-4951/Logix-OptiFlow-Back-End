package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.StaffMemberEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffMemberJpaRepository extends JpaRepository<StaffMemberEntity, UUID> {

  Optional<StaffMemberEntity> findByEmail(String email);

  List<StaffMemberEntity> findByOpticalStoreId(UUID opticalStoreId);
}
