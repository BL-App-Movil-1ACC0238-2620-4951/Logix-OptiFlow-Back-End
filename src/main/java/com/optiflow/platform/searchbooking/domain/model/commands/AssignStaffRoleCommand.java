package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.UserRole;

public record AssignStaffRoleCommand(StaffMemberId staffMemberId, UserRole role) {}
