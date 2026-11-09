package com.optiflow.platform.searchbooking.application.queryservices;

import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.queries.GetStaffMemberByIdQuery;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetStaffMemberByIdQueryService {

  private final StaffMemberRepository staffMemberRepository;

  public GetStaffMemberByIdQueryService(StaffMemberRepository staffMemberRepository) {
    this.staffMemberRepository = staffMemberRepository;
  }

  @Transactional(readOnly = true)
  public StaffMember handle(GetStaffMemberByIdQuery query) {
    return staffMemberRepository.findById(query.staffMemberId())
        .orElseThrow(() -> new DomainException("Staff member was not found.", 404));
  }
}
