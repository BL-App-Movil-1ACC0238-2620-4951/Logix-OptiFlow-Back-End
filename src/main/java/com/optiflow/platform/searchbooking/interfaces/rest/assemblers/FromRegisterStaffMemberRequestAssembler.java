package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.commands.RegisterStaffMemberCommand;
import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RegisterStaffMemberRequest;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import org.springframework.stereotype.Component;

@Component
public class FromRegisterStaffMemberRequestAssembler {

  public RegisterStaffMemberCommand toCommand(RegisterStaffMemberRequest request) {
    return new RegisterStaffMemberCommand(
        new Name(request.name()),
        new EmailAddress(request.email()),
        request.password(),
        OpticalStoreId.of(request.opticalStoreId()),
        parseRole(request.role()));
  }

  public static UserRole parseRole(String role) {
    try {
      UserRole parsed = UserRole.valueOf(role.trim().toUpperCase());
      if (parsed == UserRole.PATIENT) {
        throw new DomainException("Staff members cannot have the PATIENT role.", 400);
      }
      return parsed;
    } catch (IllegalArgumentException exception) {
      throw new DomainException(
          "Role must be OPTOMETRIST, SALES_ADVISOR, TECHNICIAN or ADMIN.", 400);
    }
  }
}
