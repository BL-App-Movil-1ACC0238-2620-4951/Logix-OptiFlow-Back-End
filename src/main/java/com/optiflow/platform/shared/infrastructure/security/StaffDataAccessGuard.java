package com.optiflow.platform.shared.infrastructure.security;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Lets a staff member read only their own profile; an administrator can read any. */
@Component
public class StaffDataAccessGuard {

  public void ensureSelfStaffAccess(AuthenticatedUser user, UUID staffMemberId) {
    if (user == null || "ADMIN".equals(user.role())) {
      return;
    }
    if (!user.userId().equals(staffMemberId)) {
      throw new DomainException("You cannot access another staff member's data.", 403);
    }
  }
}
