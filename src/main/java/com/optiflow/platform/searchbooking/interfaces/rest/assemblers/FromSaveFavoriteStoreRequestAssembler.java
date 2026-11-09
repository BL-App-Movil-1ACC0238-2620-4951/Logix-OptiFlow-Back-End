package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import com.optiflow.platform.searchbooking.domain.model.commands.SaveFavoriteOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PatientId;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.SaveFavoriteStoreRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromSaveFavoriteStoreRequestAssembler {

  public SaveFavoriteOpticalStoreCommand toCommand(UUID patientId, SaveFavoriteStoreRequest request) {
    return new SaveFavoriteOpticalStoreCommand(
        PatientId.of(patientId), OpticalStoreId.of(request.opticalStoreId()));
  }
}
