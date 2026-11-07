package com.optiflow.platform.searchbooking.application.services;

import com.optiflow.platform.searchbooking.domain.model.commands.PublishAvailableTimeSlotsCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.RateOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.model.commands.SaveFavoriteOpticalStoreCommand;
import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetAvailableTimeSlotsQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetOpticalStoreQuery;
import com.optiflow.platform.searchbooking.domain.model.entities.FavoriteStore;
import com.optiflow.platform.searchbooking.domain.model.entities.OpticalStore;
import com.optiflow.platform.searchbooking.domain.model.entities.TimeSlot;
import java.util.List;
import org.springframework.stereotype.Service;
import com.optiflow.platform.searchbooking.application.commandhandlers.PublishAvailableTimeSlotsCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.RateOpticalStoreCommandHandler;
import com.optiflow.platform.searchbooking.application.commandhandlers.SaveFavoriteOpticalStoreCommandHandler;
import com.optiflow.platform.searchbooking.application.queryservices.FilterOpticalStoresQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.GetAvailableTimeSlotsQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.GetNearbyOpticalStoresQueryService;
import com.optiflow.platform.searchbooking.application.queryservices.GetOpticalStoreQueryService;
import com.optiflow.platform.searchbooking.application.results.RatingResult;

@Service
public class OpticalStoreApplicationService {

  private final FilterOpticalStoresQueryService filterOpticalStoresQueryService;
  private final GetNearbyOpticalStoresQueryService getNearbyOpticalStoresQueryService;
  private final GetOpticalStoreQueryService getOpticalStoreQueryService;
  private final GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService;
  private final SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler;
  private final RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler;
  private final PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler;

  public OpticalStoreApplicationService(
      FilterOpticalStoresQueryService filterOpticalStoresQueryService,
      GetNearbyOpticalStoresQueryService getNearbyOpticalStoresQueryService,
      GetOpticalStoreQueryService getOpticalStoreQueryService,
      GetAvailableTimeSlotsQueryService getAvailableTimeSlotsQueryService,
      SaveFavoriteOpticalStoreCommandHandler saveFavoriteOpticalStoreCommandHandler,
      RateOpticalStoreCommandHandler rateOpticalStoreCommandHandler,
      PublishAvailableTimeSlotsCommandHandler publishAvailableTimeSlotsCommandHandler) {
    this.filterOpticalStoresQueryService = filterOpticalStoresQueryService;
    this.getNearbyOpticalStoresQueryService = getNearbyOpticalStoresQueryService;
    this.getOpticalStoreQueryService = getOpticalStoreQueryService;
    this.getAvailableTimeSlotsQueryService = getAvailableTimeSlotsQueryService;
    this.saveFavoriteOpticalStoreCommandHandler = saveFavoriteOpticalStoreCommandHandler;
    this.rateOpticalStoreCommandHandler = rateOpticalStoreCommandHandler;
    this.publishAvailableTimeSlotsCommandHandler = publishAvailableTimeSlotsCommandHandler;
  }

  public List<OpticalStore> findStores(FilterOpticalStoresQuery query) {
    return filterOpticalStoresQueryService.handle(query);
  }

  public List<OpticalStore> findNearby(GetNearbyOpticalStoresQuery query) {
    return getNearbyOpticalStoresQueryService.handle(query);
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
