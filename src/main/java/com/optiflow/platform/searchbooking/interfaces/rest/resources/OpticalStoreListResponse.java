package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpticalStoreListResponse(String message, List<OpticalStoreResponse> opticalStores) {
}
