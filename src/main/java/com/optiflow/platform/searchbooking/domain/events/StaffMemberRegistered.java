package com.optiflow.platform.searchbooking.domain.events;

import com.optiflow.platform.searchbooking.domain.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.valueobjects.UserRole;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record StaffMemberRegistered(
    StaffMemberId staffMemberId,
    OpticalStoreId opticalStoreId,
    EmailAddress email,
    UserRole role,
    Instant occurredAt)
    implements DomainEvent {}
