package com.optiflow.platform.searchbooking.application.queries;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import java.util.Optional;

public record GetStaffMembersQuery(Optional<OpticalStoreId> opticalStoreId) {}
