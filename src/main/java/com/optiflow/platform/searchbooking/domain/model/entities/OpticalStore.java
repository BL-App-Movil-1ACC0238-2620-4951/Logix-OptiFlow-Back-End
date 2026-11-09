package com.optiflow.platform.searchbooking.domain.model.entities;

import com.optiflow.platform.searchbooking.domain.model.valueobjects.GeoLocation;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreName;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StoreStatus;
import java.util.Optional;

public class OpticalStore {

  private final OpticalStoreId id;
  private final StoreName name;
  private final StoreAddress address;
  private final PhoneNumber phone;
  private StoreRating rating;
  private final StoreStatus status;
  private final GeoLocation location;

  private OpticalStore(
      OpticalStoreId id,
      StoreName name,
      StoreAddress address,
      PhoneNumber phone,
      StoreRating rating,
      StoreStatus status,
      GeoLocation location) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.rating = rating;
    this.status = status;
    this.location = location;
  }

  public static OpticalStore reconstitute(
      OpticalStoreId id,
      StoreName name,
      StoreAddress address,
      PhoneNumber phone,
      StoreRating rating,
      StoreStatus status,
      GeoLocation location) {
    return new OpticalStore(id, name, address, phone, rating, status, location);
  }

  public void updateRating(StoreRating rating) {
    this.rating = rating;
  }

  public boolean isActive() {
    return status == StoreStatus.ACTIVE;
  }

  public OpticalStoreId id() {
    return id;
  }

  public StoreName name() {
    return name;
  }

  public StoreAddress address() {
    return address;
  }

  public PhoneNumber phone() {
    return phone;
  }

  public StoreRating rating() {
    return rating;
  }

  public StoreStatus status() {
    return status;
  }

  public Optional<GeoLocation> location() {
    return Optional.ofNullable(location);
  }
}
