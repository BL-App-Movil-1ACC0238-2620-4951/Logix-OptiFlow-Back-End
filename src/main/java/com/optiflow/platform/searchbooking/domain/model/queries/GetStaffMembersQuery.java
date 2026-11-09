package com.optiflow.platform.searchbooking.domain.model.queries;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import java.util.Optional;

public record GetStaffMembersQuery(Optional<OpticalStoreId> opticalStoreId) {}
