package com.optiflow.platform.inventory.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "frame_models")
public class FrameModelEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true, length = 100)
  private String sku;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(length = 100)
  private String material;

  @Column(length = 100)
  private String shape;

  @Column(length = 100)
  private String color;

  @Column(name = "price_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal priceAmount;

  @Column(name = "price_currency", nullable = false, length = 10)
  private String priceCurrency;

  @Column(name = "try_on_model_url", length = 255)
  private String tryOnModelUrl;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getShape() {
    return shape;
  }

  public void setShape(String shape) {
    this.shape = shape;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public BigDecimal getPriceAmount() {
    return priceAmount;
  }

  public void setPriceAmount(BigDecimal priceAmount) {
    this.priceAmount = priceAmount;
  }

  public String getPriceCurrency() {
    return priceCurrency;
  }

  public void setPriceCurrency(String priceCurrency) {
    this.priceCurrency = priceCurrency;
  }

  public String getTryOnModelUrl() {
    return tryOnModelUrl;
  }

  public void setTryOnModelUrl(String tryOnModelUrl) {
    this.tryOnModelUrl = tryOnModelUrl;
  }
}
