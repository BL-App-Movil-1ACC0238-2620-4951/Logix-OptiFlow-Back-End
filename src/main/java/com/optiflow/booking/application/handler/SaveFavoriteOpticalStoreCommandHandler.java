package com.optiflow.booking.application.handler;

import com.optiflow.booking.application.DomainEventPublisher;
import com.optiflow.booking.application.command.SaveFavoriteOpticalStoreCommand;
import com.optiflow.booking.domain.event.FavoriteOpticalStoreSaved;
import com.optiflow.booking.domain.exception.DomainException;
import com.optiflow.booking.domain.model.FavoriteStore;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.repository.FavoriteStoreRepository;
import com.optiflow.booking.domain.repository.OpticalStoreRepository;
import com.optiflow.booking.domain.repository.PatientRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveFavoriteOpticalStoreCommandHandler {

  private final PatientRepository patientRepository;
  private final OpticalStoreRepository opticalStoreRepository;
  private final FavoriteStoreRepository favoriteStoreRepository;
  private final DomainEventPublisher eventPublisher;
  private final Clock clock;

  public SaveFavoriteOpticalStoreCommandHandler(
      PatientRepository patientRepository,
      OpticalStoreRepository opticalStoreRepository,
      FavoriteStoreRepository favoriteStoreRepository,
      DomainEventPublisher eventPublisher,
      Clock clock) {
    this.patientRepository = patientRepository;
    this.opticalStoreRepository = opticalStoreRepository;
    this.favoriteStoreRepository = favoriteStoreRepository;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public FavoriteStore handle(SaveFavoriteOpticalStoreCommand command) {
    patientRepository.findById(command.patientId())
        .orElseThrow(() -> new DomainException("Patient was not found.", 404));
    OpticalStore store = opticalStoreRepository.findById(command.opticalStoreId())
        .orElseThrow(() -> new DomainException("Optical store was not found.", 404));
    if (!store.isActive()) {
      throw new DomainException("The optical store is not available.", 409);
    }
    favoriteStoreRepository.find(command.patientId(), command.opticalStoreId()).ifPresent(existing -> {
      throw new DomainException("This optical store is already a favorite.", 409);
    });
    Instant now = clock.instant();
    FavoriteStore favorite = FavoriteStore.save(command.patientId(), command.opticalStoreId(), now);
    favoriteStoreRepository.save(favorite);
    eventPublisher.publish(
        new FavoriteOpticalStoreSaved(command.patientId(), command.opticalStoreId(), now));
    return favorite;
  }
}
