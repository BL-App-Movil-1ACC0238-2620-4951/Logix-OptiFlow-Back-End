package com.optiflow.booking.application;

import com.optiflow.booking.domain.event.DomainEvent;

public interface DomainEventPublisher {

  void publish(DomainEvent event);
}
