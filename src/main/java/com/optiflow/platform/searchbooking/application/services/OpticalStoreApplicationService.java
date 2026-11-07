package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.application.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.application.commands.RateOpticalStoreCommand;
import com.optiflow.platform.searchbooking.application.commands.SaveFavoriteOpticalStoreCommand;
import com.optiflow.platform.searchbooking.application.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.application.queries.GetAvailableTimeSlotsQuery;
import com.optiflow.platform.searchbooking.application.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.entities.TimeSlot;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OpticalStoreApplicationService {

  private final FilterOpticalStoresQueryService filterOpticalStoresQueryService;
  private final GetOpticalStoreQueryService getOpticalStoreQueryService;
  private final GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService;
  private final SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler;
  private final RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler;
  private final PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler;

  public OpticalStoreApplicationService(
      FilterOpticalStoresQueryService filterOpticalStoresQueryService,
      GetOpticalStoreQueryService getOpticalStoreQueryService,
      GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService,
      SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler,
      RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler,
      PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler) {
    this.filterOpticalStoresQueryService = filterOpticalStoresQueryService;
    this.getOpticalStoreQueryService = getOpticalStoreQueryService;
    this.getAvailableTimeSlotsQueryService = getAvailableTimeSlotsQueryService;
    this.saveFavoriteOpticalStoreCommandHandler = saveFavoriteOpticalStoreCommandHandler;
    this.rateOpticalStoreCommandHandler = rateOpticalStoreCommandHandler;
    this.publishAvailableTimeSlotsCommandHandler = publishAvailableTimeSlotsCommandHandler;
  }

  public List<OpticalStore> findStores(FilterOpticalStoresQuery query) {
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

  public List<TimeSlot> publishTimeSlots(PublishAvailableTimeSlotsCommand command) {
    return publishAvailableTimeSlotsCommandHandler.handle(command);
  }
}
