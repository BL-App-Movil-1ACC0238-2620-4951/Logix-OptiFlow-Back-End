package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMembersQuery;
import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
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
