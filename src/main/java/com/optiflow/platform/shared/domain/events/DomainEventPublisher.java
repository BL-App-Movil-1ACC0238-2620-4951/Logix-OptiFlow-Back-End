package com.optiflow.platform.shared.domain.events;

public interface DomainEventPublisher {

  void publish(DomainEvent event);
}
