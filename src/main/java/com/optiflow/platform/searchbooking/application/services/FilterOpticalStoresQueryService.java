package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.events.OpticalStoresFiltered;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilterOpticalStoresQueryService {

  private final OpticalStoreRepository opticalStoreRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public FilterOpticalStoresQueryService(
      OpticalStoreRepository opticalStoreRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public List<OpticalStore> handle(FilterOpticalStoresQuery query) {
    List<OpticalStore> stores = opticalStoreRepository.filter(
        query.name(), query.address(), query.minRating());
    eventPublisher.publish(new OpticalStoresFiltered(query.name(), query.address(), clock.instant()));
    return stores;
  }
}
