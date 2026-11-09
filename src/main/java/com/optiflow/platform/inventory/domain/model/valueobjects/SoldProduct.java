package com.optiflow.platform.inventory.domain.model.valueobjects;

/** Product of a closed sale, translated from the Clinical &amp; Commercial event. */
public record SoldProduct(Sku sku, int quantity) {
}
