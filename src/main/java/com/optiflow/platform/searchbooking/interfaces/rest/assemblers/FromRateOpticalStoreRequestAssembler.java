package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.application.commands.RateOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.RateOpticalStoreRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromRateOpticalStoreRequestAssembler {

  public RateOpticalStoreCommand toCommand(UUID opticalStoreId, RateOpticalStoreRequest request) {
    String comment = request.comment() == null || request.comment().isBlank()
        ? null
        : request.comment().trim();
    return new RateOpticalStoreCommand(
        PatientId.of(request.patientId()),
        OpticalStoreId.of(opticalStoreId),
        StoreRating.given(request.score()),
        comment);
  }
}
