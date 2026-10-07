package com.optiflow.platform.searchbooking.domain.entities;

import com.optiflow.platform.searchbooking.domain.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.valueobjects.PhoneNumber;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreAddress;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreName;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreRating;
import com.optiflow.platform.searchbooking.domain.valueobjects.StoreStatus;

public class OpticalStore {

  private final OpticalStoreId id;
  private final StoreName name;
  private final StoreAddress address;
  private final PhoneNumber phone;
  private StoreRating rating;
  private final StoreStatus status;

  private OpticalStore(
      OpticalStoreId id,
      StoreName name,
      StoreAddress address,
      PhoneNumber phone,
      StoreRating rating,
      StoreStatus status) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.rating = rating;
    this.status = status;
  }

  public static OpticalStore reconstitute(
      OpticalStoreId id,
      StoreName name,
      StoreAddress address,
      PhoneNumber phone,
      StoreRating rating,
      StoreStatus status) {
    return new OpticalStore(id, name, address, phone, rating, status);
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
}
