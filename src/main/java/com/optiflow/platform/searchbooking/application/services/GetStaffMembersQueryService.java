package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetStaffMembersQuery;
import com.optiflow.platform.searchbooking.domain.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetStaffMembersQueryService {

  private final StaffMemberRepository staffMemberRepository;

  public GetStaffMembersQueryService(StaffMemberRepository staffMemberRepository) {
    this.staffMemberRepository = staffMemberRepository;
  }

  @Transactional(readOnly = true)
  public List<StaffMember> handle(GetStaffMembersQuery query) {
    return query.opticalStoreId()
        .map(staffMemberRepository::findByOpticalStoreId)
        .orElseGet(staffMemberRepository::findAll);
  }
}
