package com.optiflow.platform.searchbooking.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record OpticalStoreResponse(
    UUID id, String name, String address, String phone, BigDecimal rating, String status) {
}
