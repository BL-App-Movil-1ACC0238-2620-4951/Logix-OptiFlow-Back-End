package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.RateOpticalStoreCommand;
import com.optiflow.booking.application.result.RatingResult;
import com.optiflow.booking.domain.event.OpticalStoreRated;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.model.PatientStoreRating;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
import com.optiflow.booking.domain.repository.PatientRepository;
import com.optiflow.booking.domain.repository.StoreRatingRepository;
import com.optiflow.booking.domain.service.StoreRatingService;
import com.optiflow.booking.domain.vo.StoreRating;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RateOpticalStoreCommandHandler {

  private final PatientRepository patientRepository;
  private final OpticalStoreRepository opticalStoreRepository;
  private final StoreRatingRepository storeRatingRepository;
  private final StoreRatingService storeRatingService;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public RateOpticalStoreCommandHandler(
      PatientRepository patientRepository,
      OpticalStoreRepository opticalStoreRepository,
      StoreRatingRepository storeRatingRepository,
      StoreRatingService storeRatingService,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.opticalStoreRepository = opticalStoreRepository;
    this.storeRatingRepository = storeRatingRepository;
    this.storeRatingService = storeRatingService;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public RatingResult handle(RateOpticalStoreCommand command) {
    patientRepository.findById(command.patientId())
        .orElseThrow(() -> new DomainException("Patient was not found.", 404));
    OpticalStore store = opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    if (!store.isActive()) {
      throw new DomainException("The optical store is not available.", 409);
    }
    storeRatingService.ensureNotRated(
        storeRatingRepository.find(command.patientId(), command.opticalStoreId()).isPresent());
    Instant now = clock.instant();
    PatientStoreRating rating = PatientStoreRating.create(
        command.patientId(),
        command.opticalStoreId(),
        command.score(),
        command.comment(),
        now);
    storeRatingRepository.save(rating);
    List<PatientStoreRating> ratings = storeRatingRepository.findByStore(command.opticalStoreId());
    StoreRating average = storeRatingService.average(ratings);
    store.updateRating(average);
    opticalStoreRepository.save(store);
    eventPublisher.publish(new OpticalStoreRated(command.patientId(), command.opticalStoreId(), now));
    return new RatingResult(rating, average);
  }
}
