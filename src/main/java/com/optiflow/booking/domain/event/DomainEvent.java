package com.optiflow.booking.domain.event;

import java.time.Instant;

public interface DomainEvent {

  Instant occurredAt();
}
