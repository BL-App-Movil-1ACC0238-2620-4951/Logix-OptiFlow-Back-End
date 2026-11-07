package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.UserRole;

public record RegisterStaffMemberCommand(
    Name name,
    EmailAddress email,
    String password,
    OpticalStoreId opticalStoreId,
    UserRole role) {}
