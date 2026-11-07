package com.optiflow.platform.searchbooking.domain.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import java.util.List;
import java.util.Optional;

public interface StaffMemberRepository {

  void save(StaffMember staffMember, String passwordHash);

  void update(StaffMember staffMember);

  Optional<StaffMember> findById(StaffMemberId id);

  Optional<StaffMember> findByEmail(EmailAddress email);

  Optional<String> findPasswordHashByEmail(EmailAddress email);

  List<StaffMember> findByOpticalStoreId(OpticalStoreId opticalStoreId);

  List<StaffMember> findAll();
}
