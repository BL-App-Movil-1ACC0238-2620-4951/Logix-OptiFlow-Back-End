package com.optiflow.platform.searchbooking.application.commands;

import com.optiflow.platform.searchbooking.domain.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;

public record AssignStaffRoleCommand(StaffMemberId staffMemberId, UserRole role) {}
