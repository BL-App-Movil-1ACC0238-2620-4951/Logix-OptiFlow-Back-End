package com.optiflow.platform.searchbooking.domain.model.commands;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;

public record UpdateStaffMemberStatusCommand(StaffMemberId staffMemberId, boolean active) {}
