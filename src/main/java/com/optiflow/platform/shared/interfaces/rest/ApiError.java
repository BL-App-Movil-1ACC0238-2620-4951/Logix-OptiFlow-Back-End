package com.optiflow.platform.shared.interfaces.rest;

public record ApiError(int status, String error, String message) {
}
