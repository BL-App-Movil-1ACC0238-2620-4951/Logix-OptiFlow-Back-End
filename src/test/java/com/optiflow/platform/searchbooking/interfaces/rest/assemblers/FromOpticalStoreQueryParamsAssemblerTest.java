package com.optiflow.platform.searchbooking.interfaces.rest.assemblers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.searchbooking.domain.model.queries.FilterOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.GetNearbyOpticalStoresQuery;
import com.optiflow.platform.searchbooking.domain.model.queries.SearchOpticalStoresQuery;
import com.optiflow.platform.searchbooking.interfaces.rest.resources.OpticalStoreQueryParams;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FromOpticalStoreQueryParamsAssemblerTest {

  private final FromOpticalStoreQueryParamsAssembler assembler =
      new FromOpticalStoreQueryParamsAssembler();

  @Test
  void searchesWhenThereAreNoParameters() {
    assertEquals(new SearchOpticalStoresQuery(null, null),
        assembler.toQuery(new OpticalStoreQueryParams(null, null, null, null, null, null)));
  }

  @Test
  void searchesByNameAndAddress() {
    assertEquals(new SearchOpticalStoresQuery("lux", "miraflores"),
        assembler.toQuery(new OpticalStoreQueryParams("lux", "miraflores", null, null, null, null)));
  }

  @Test
  void filtersWhenAMinimumRatingIsGiven() {
    assertInstanceOf(FilterOpticalStoresQuery.class, assembler.toQuery(
        new OpticalStoreQueryParams("lux", null, BigDecimal.valueOf(4), null, null, null)));
  }

  @Test
  void looksForNearbyStoresWhenALocationIsGiven() {
    assertInstanceOf(GetNearbyOpticalStoresQuery.class, assembler.toQuery(new OpticalStoreQueryParams(
        null, null, BigDecimal.valueOf(4), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN)));
  }

  @Test
  void rejectsAnIncompleteLocation() {
    assertThrows(DomainException.class, () -> assembler.toQuery(
        new OpticalStoreQueryParams(null, null, null, BigDecimal.ONE, null, BigDecimal.TEN)));
  }
}
