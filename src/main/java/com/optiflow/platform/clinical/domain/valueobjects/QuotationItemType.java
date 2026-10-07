package com.optiflow.platform.clinical.domain.valueobjects;

import com.optiflow.platform.shared.exceptions.DomainException;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum QuotationItemType {
  FRAME, LENS, TREATMENT, SERVICE;

  public static QuotationItemType from(String value) {
    return Arrays.stream(values())
        .filter(candidate -> candidate.name().equalsIgnoreCase(value == null ? "" : value.trim()))
        .findFirst()
        .orElseThrow(() -> new DomainException(
            "Item type must be one of: "
                + Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "))
                + ".",
            400));
  }
}
