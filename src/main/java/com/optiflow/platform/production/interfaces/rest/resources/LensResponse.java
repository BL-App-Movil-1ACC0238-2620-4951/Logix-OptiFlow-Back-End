package com.optiflow.platform.production.interfaces.rest.resources;

import java.util.UUID;

public record LensResponse(UUID id, String specifications, boolean completed) {
}
