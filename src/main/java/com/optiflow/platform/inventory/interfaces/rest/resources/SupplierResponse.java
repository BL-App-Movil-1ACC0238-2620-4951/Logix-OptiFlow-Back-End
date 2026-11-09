package com.optiflow.platform.inventory.interfaces.rest.resources;

import java.util.UUID;

public record SupplierResponse(
    UUID id, String name, String contact) {
}
