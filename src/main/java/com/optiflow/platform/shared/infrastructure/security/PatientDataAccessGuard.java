package com.optiflow.platform.shared.infrastructure.security;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PatientDataAccessGuard {

  public void ensureSelfPatientAccess(AuthenticatedUser user, UUID patientId) {
    if (user == null) {
      return;
    }
    if ("PATIENT".equals(user.role()) && !user.userId().equals(patientId)) {
      throw new DomainException("You cannot access another patient's data.", 403);
    }
  }
}
