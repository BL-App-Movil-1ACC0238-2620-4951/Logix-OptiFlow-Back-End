package com.optiflow.platform.searchbooking.infrastructure.outboundservices;

import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.shared.infrastructure.security.AccountStatusChecker;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** A deactivated staff member loses access at once, even with a token issued before. */
@Component
public class AccountStatusAdapter implements AccountStatusChecker {

  private final StaffMemberRepository staffMemberRepository;

  public AccountStatusAdapter(StaffMemberRepository staffMemberRepository) {
    this.staffMemberRepository = staffMemberRepository;
  }

  @Override
  public boolean isActive(UUID userId, String role) {
    if ("PATIENT".equals(role)) {
      return true;
    }
    return staffMemberRepository.findById(StaffMemberId.of(userId))
        .map(StaffMember::active)
        .orElse(false);
  }
}
