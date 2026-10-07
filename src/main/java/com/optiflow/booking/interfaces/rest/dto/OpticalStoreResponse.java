package com.optiflow.booking.interfaces.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OpticalStoreResponse(
    UUID id, String name, String address, String phone, BigDecimal rating, String status) {
}
