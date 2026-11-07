package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.Name;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;

public record RegisterStaffMemberCommand(
    Name name,
    EmailAddress email,
    String password,
    OpticalStoreId opticalStoreId,
    UserRole role) {}
