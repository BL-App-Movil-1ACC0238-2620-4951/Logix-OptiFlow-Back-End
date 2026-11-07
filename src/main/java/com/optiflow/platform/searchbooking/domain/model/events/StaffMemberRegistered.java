package com.optiflow.platform.searchbooking.domain.model.events;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.UserRole;
import com.optiflow.platform.shared.domain.events.DomainEvent;
import java.time.Instant;

public record StaffMemberRegistered(
    StaffMemberId staffMemberId,
    OpticalStoreId opticalStoreId,
    EmailAddress email,
    UserRole role,
    Instant occurredAt)
    implements DomainEvent {}
