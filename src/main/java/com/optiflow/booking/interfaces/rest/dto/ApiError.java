package com.optiflow.booking.interfaces.rest.dto;

public record ApiError(int status, String error, String message) {
}
