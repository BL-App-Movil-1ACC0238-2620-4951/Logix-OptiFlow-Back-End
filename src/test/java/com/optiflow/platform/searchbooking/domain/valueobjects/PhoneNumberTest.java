package com.optiflow.platform.searchbooking.domain.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.optiflow.platform.shared.exceptions.DomainException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class PhoneNumberTest {

  @ParameterizedTest
  @CsvSource({
      "999888777, 999888777",
      "999 888 777, 999888777",
      "+51 999-888-777, 51999888777",
      "+1234567, 1234567",
      "123456789012345, 123456789012345"
  })
  void acceptsSupportedFormatsAndNormalizesDigits(String input, String expected) {
    assertEquals(expected, new PhoneNumber(input).value());
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  @ValueSource(strings = {
      "   ", "abc999888777", "999abc888777", "999888777abc",
      "999@888777", "999.888.777", "999+888777", "++51999888777",
      "999--888777", "-999888777", "999888777-", "123456", "1234567890123456"
  })
  void rejectsInvalidCharactersFormatsAndLengths(String input) {
    DomainException error = assertThrows(DomainException.class, () -> new PhoneNumber(input));
    assertEquals(400, error.status());
  }
}
