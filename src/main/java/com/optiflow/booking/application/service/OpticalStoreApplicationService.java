package com.optiflow.booking.application.service;

import com.optiflow.booking.application.command.PublishAvailableTimeSlotsCommand;
import com.optiflow.booking.application.command.RateOpticalStoreCommand;
import com.optiflow.booking.application.command.SaveFavoriteOpticalStoreCommand;
import com.optiflow.booking.application.handler.FilterOpticalStoresQueryService;
import com.optiflow.booking.application.handler.GetAvailableTimeSlotsQueryService;
import com.optiflow.booking.application.handler.GetOpticalStoreQueryService;
import com.optiflow.booking.application.handler.PublishAvailableTimeSlotsCommandHandler;
import com.optiflow.booking.application.handler.RateOpticalStoreCommandHandler;
import com.optiflow.booking.application.handler.SaveFavoriteOpticalStoreCommandHandler;
import com.optiflow.booking.application.handler.SearchOpticalStoresQueryService;
import com.optiflow.booking.application.query.FilterOpticalStoresQuery;
import com.optiflow.booking.application.query.GetAvailableTimeSlotsQuery;
import com.optiflow.booking.application.query.GetOpticalStoreQuery;
import com.optiflow.booking.application.query.SearchOpticalStoresQuery;
import com.optiflow.booking.application.result.RatingResult;
import com.optiflow.booking.domain.model.FavoriteStore;
import com.optiflow.booking.domain.model.OpticalStore;
import com.optiflow.booking.domain.model.TimeSlot;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OpticalStoreApplicationService {

  private final SearchOpticalStoresQueryService searchOpticalStoresQueryService;
  private final FilterOpticalStoresQueryService filterOpticalStoresQueryService;
  private final GetOpticalStoreQueryService getOpticalStoreQueryService;
  private final GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService;
  private final SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler;
  private final RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler;
  private final PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler;

  public OpticalStoreApplicationService(
      SearchOpticalStoresQueryService searchOpticalStoresQueryService,
      FilterOpticalStoresQueryService filterOpticalStoresQueryService,
      GetOpticalStoreQueryService getOpticalStoreQueryService,
      GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService,
      SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler,
      RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler,
      PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler) {
    this.searchOpticalStoresQueryService = searchOpticalStoresQueryService;
    this.filterOpticalStoresQueryService = filterOpticalStoresQueryService;
    this.getOpticalStoreQueryService = getOpticalStoreQueryService;
    this.getAvailableTimeSlotsQueryService = getAvailableTimeSlotsQueryService;
    this.saveFavoriteOpticalStoreCommandHandler = saveFavoriteOpticalStoreCommandHandler;
    this.rateOpticalStoreCommandHandler = rateOpticalStoreCommandHandler;
    this.publishAvailableTimeSlotsCommandHandler = publishAvailableTimeSlotsCommandHandler;
  }

  public List<OpticalStore> search(SearchOpticalStoresQuery query) {
    return searchOpticalStoresQueryService.handle(query);
  }

  public List<OpticalStore> filter(FilterOpticalStoresQuery query) {
    return filterOpticalStoresQueryService.handle(query);
  }

  public OpticalStore get(GetOpticalStoreQuery query) {
    return getOpticalStoreQueryService.handle(query);
  }

  public List<TimeSlot> availability(GetAvailableTimeSlotsQuery query) {
    return getAvailableTimeSlotsQueryService.handle(query);
  }

  public FavoriteStore saveFavorite(SaveFavoriteOpticalStoreCommand command) {
    return saveFavoriteOpticalStoreCommandHandler.handle(command);
  }

  public RatingResult rate(RateOpticalStoreCommand command) {
    return rateOpticalStoreCommandHandler.handle(command);
  }

  public void publishTimeSlots(PublishAvailableTimeSlotsCommand command) {
    publishAvailableTimeSlotsCommandHandler.handle(command);
  }
}
