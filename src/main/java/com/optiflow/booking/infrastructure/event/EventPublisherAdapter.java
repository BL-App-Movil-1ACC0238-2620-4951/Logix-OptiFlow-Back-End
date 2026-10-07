package com.optiflow.booking.infrastructure.event;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherAdapter implements DomainEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public EventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publish(DomainEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}
