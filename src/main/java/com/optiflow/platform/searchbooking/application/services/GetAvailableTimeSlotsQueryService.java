package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.queries.GetAvailableTimeSlotsQuery;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.TimeSlotRepository;
import com.optiflow.platform.shared.exceptions.DomainException;
import java.time.Clock;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAvailableTimeSlotsQueryService {

  private final OpticalStoreRepository opticalStoreRepository;
  private final TimeSlotRepository timeSlotRepository;
  private final Clock clock;

  public GetAvailableTimeSlotsQueryService(
      OpticalStoreRepository opticalStoreRepository,
      TimeSlotRepository timeSlotRepository,
      Clock clock) {
    this.opticalStoreRepository = opticalStoreRepository;
    this.timeSlotRepository = timeSlotRepository;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public List<TimeSlot> handle(GetAvailableTimeSlotsQuery query) {
    opticalStoreRepository.findById(query.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    return timeSlotRepository.findAvailableByStore(query.opticalStoreId(), clock.instant());
  }
}
