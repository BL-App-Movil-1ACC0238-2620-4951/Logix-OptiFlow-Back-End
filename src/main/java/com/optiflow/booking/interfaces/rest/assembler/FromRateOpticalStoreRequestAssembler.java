package com.optiflow.booking.interfaces.rest.assembler;

import com.optiflow.booking.application.command.RateOpticalStoreCommand;
import com.optiflow.booking.domain.vo.OpticalStoreId;
import com.optiflow.booking.domain.vo.PatientId;
import com.optiflow.booking.domain.vo.StoreRating;
import com.optiflow.booking.interfaces.rest.dto.RateOpticalStoreRequest;
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
