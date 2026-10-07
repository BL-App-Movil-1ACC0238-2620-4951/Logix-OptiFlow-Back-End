package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.RateOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.entities.PatientStoreRating;
import com.optiflow.platform.searchbooking.domain.events.OpticalStoreRated;
import com.optiflow.platform.searchbooking.domain.repositories.OpticalStoreRepository;
import com.optiflow.platform.searchbooking.domain.repositories.PatientRepository;
import com.optiflow.platform.searchbooking.domain.repositories.StoreRatingRepository;
import com.optiflow.platform.searchbooking.domain.services.StoreRatingService;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import com.optiflow.platform.shared.domain.events.DomainEventPublisher;
import com.optiflow.platform.shared.exceptions.DomainException;
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
