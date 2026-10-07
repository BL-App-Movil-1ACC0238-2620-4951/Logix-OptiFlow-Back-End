package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.services.AppointmentDetails;
import com.optiflow.platform.searchbooking.application.services.LoginResult;
import com.optiflow.platform.searchbooking.application.services.RatingResult;
import com.optiflow.platform.searchbooking.domain.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.entities.Patient;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.AppointmentResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.FavoriteStoreResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.LoginResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreListResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.PatientResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.StoreRatingResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.TimeSlotListResponse;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.TimeSlotResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SearchBookingResponseAssembler {

  public PatientResponse toPatientResponse(Patient patient) {
    return new PatientResponse(
        patient.id().value(),
        patient.name().value(),
        patient.email().value(),
        patient.phone().value(),
        patient.createdAt());
  }

  public LoginResponse toLoginResponse(LoginResult result) {
    return new LoginResponse(result.token(), toPatientResponse(result.patient()));
  }

  public OpticalStoreResponse toStoreResponse(OpticalStore store) {
    return new OpticalStoreResponse(
        store.id().value(),
        store.name().value(),
        store.address().value(),
        store.phone().value(),
        store.rating().value(),
        store.status().name());
  }

  public OpticalStoreListResponse toStoreList(List<OpticalStore> stores, String emptyMessage) {
    if (stores.isEmpty()) {
      return new OpticalStoreListResponse(emptyMessage, List.of());
    }
    return new OpticalStoreListResponse(
        null, stores.stream().map(this::toStoreResponse).toList());
  }

  public TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot) {
    return new TimeSlotResponse(
        timeSlot.id().value(),
        timeSlot.opticalStoreId().value(),
        timeSlot.startDateTime(),
        timeSlot.endDateTime(),
        timeSlot.status().name());
  }

  public TimeSlotListResponse toTimeSlotList(List<TimeSlot> timeSlots) {
    if (timeSlots.isEmpty()) {
      return new TimeSlotListResponse("No time slots are available for this optical store.", List.of());
    }
    return new TimeSlotListResponse(null, timeSlots.stream().map(this::toTimeSlotResponse).toList());
  }

  public AppointmentResponse toAppointmentResponse(AppointmentDetails details) {
    return new AppointmentResponse(
        details.appointment().id().value(),
        details.appointment().patientId().value(),
        details.appointment().opticalStoreId().value(),
        details.appointment().timeSlotId().value(),
        details.appointment().status().name(),
        details.timeSlot().startDateTime(),
        details.timeSlot().endDateTime(),
        details.appointment().createdAt(),
        details.appointment().updatedAt());
  }

  public FavoriteStoreResponse toFavoriteResponse(FavoriteStore favoriteStore) {
    return new FavoriteStoreResponse(
        favoriteStore.patientId().value(),
        favoriteStore.opticalStoreId().value(),
        favoriteStore.savedAt());
  }

  public StoreRatingResponse toRatingResponse(RatingResult result) {
    return new StoreRatingResponse(
        result.rating().patientId().value(),
        result.rating().opticalStoreId().value(),
        result.rating().score().value(),
        result.averageRating().value());
  }
}
