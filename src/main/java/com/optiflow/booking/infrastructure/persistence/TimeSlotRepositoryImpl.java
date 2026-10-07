package com.optiflow.booking.infrastructure.persistence;

import com.optiflow.booking.domain.model.TimeSlot;
import com.optiflow.booking.domain.repository.TimeSlotRepository;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.TimeSlotId;
import com.optiflow.booking.infrastructure.persistence.jpa.TimeSlotJpaRepository;
import com.optiflow.booking.infrastructure.persistence.mapper.TimeSlotMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class TimeSlotRepositoryImpl implements TimeSlotRepository {

  private final TimeSlotJpaRepository timeSlotJpaRepository;
  private final TimeSlotMapper timeSlotMapper;

  public TimeSlotRepositoryImpl(
      TimeSlotJpaRepository timeSlotJpaRepository, TimeSlotMapper timeSlotMapper) {
    this.timeSlotJpaRepository = timeSlotJpaRepository;
    this.timeSlotMapper = timeSlotMapper;
  }

  @Override
  public void save(TimeSlot timeSlot) {
    timeSlotJpaRepository.save(timeSlotMapper.toEntity(timeSlot));
  }

  @Override
  public Optional<TimeSlot> findById(TimeSlotId id) {
    return timeSlotJpaRepository.findById(id.value()).map(timeSlotMapper::toDomain);
  }

  @Override
  public Optional<TimeSlot> findByIdForUpdate(TimeSlotId id) {
    return timeSlotJpaRepository.findByIdForUpdate(id.value()).map(timeSlotMapper::toDomain);
  }

  @Override
  public List<TimeSlot> findAvailableByStore(OpticalStoreId opticalStoreId, Instant now) {
    return timeSlotJpaRepository.findAvailableByStore(opticalStoreId.value(), now).stream()
        .map(timeSlotMapper::toDomain)
        .toList();
  }
}
