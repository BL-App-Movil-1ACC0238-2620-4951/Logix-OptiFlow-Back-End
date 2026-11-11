package com.optiflow.platform.inventory.domain.model.aggregates;

import com.optiflow.platform.inventory.domain.model.valueobjects.FrameModelId;
import com.optiflow.platform.inventory.domain.model.valueobjects.Price;
import com.optiflow.platform.inventory.domain.model.valueobjects.Sku;
import com.optiflow.platform.shared.domain.exceptions.DomainException;

/** Frame model of the catalog, identified by the SKU that the scanner reads. */
public class FrameModel {

  private final FrameModelId id;
  private final Sku sku;
  private final String name;
  private final String material;
  private final String shape;
  private final String color;
  private Price price;
  private String tryOnModelUrl;

  private FrameModel(
      FrameModelId id,
      Sku sku,
      String name,
      String material,
      String shape,
      String color,
      Price price,
      String tryOnModelUrl) {
    if (id == null || sku == null || price == null) {
      throw new DomainException("Frame model id, SKU and price are required.", 400);
    }
    if (name == null || name.isBlank() || name.trim().length() > 255) {
      throw new DomainException("Frame model name is required (max. 255 characters).", 400);
    }
    this.id = id;
    this.sku = sku;
    this.name = name.trim();
    this.material = optional(material, 100, "Material");
    this.shape = optional(shape, 100, "Shape");
    this.color = optional(color, 100, "Color");
    this.price = price;
    this.tryOnModelUrl = optional(tryOnModelUrl, 255, "Try-on model URL");
  }

  public static FrameModel add(
      Sku sku, String name, String material, String shape, String color, Price price,
      String tryOnModelUrl) {
    return new FrameModel(
        FrameModelId.generate(), sku, name, material, shape, color, price, tryOnModelUrl);
  }

  public static FrameModel reconstitute(
      FrameModelId id, Sku sku, String name, String material, String shape, String color,
      Price price, String tryOnModelUrl) {
    return new FrameModel(id, sku, name, material, shape, color, price, tryOnModelUrl);
  }

  public void updatePrice(Price newPrice) {
    if (newPrice == null) {
      throw new DomainException("Price is required.", 400);
    }
    this.price = newPrice;
  }

  /** Assigns the virtual try-on model of the frame; a blank value removes it. */
  public void updateTryOnModel(String newTryOnModelUrl) {
    this.tryOnModelUrl = optional(newTryOnModelUrl, 255, "Try-on model URL");
  }

  private static String optional(String value, int maxLength, String field) {
    if (value == null || value.isBlank()) {
      return null;
    }
    if (value.trim().length() > maxLength) {
      throw new DomainException(field + " must have at most " + maxLength + " characters.", 400);
    }
    return value.trim();
  }

  public FrameModelId id() {
    return id;
  }

  public Sku sku() {
    return sku;
  }

  public String name() {
    return name;
  }

  public String material() {
    return material;
  }

  public String shape() {
    return shape;
  }

  public String color() {
    return color;
  }

  public Price price() {
    return price;
  }

  public String tryOnModelUrl() {
    return tryOnModelUrl;
  }
}
