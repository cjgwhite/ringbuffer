package com.phoundation.ring;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RingBufferTest {

  @ParameterizedTest
  @CsvSource(
      delimiterString = "|",
      useHeadersInDisplayName = true,
      textBlock = """
          capacity  | blocking
          -1        | true
          0         | true
          -1        | false
          0         | false
          """

  )
  @DisplayName("""
      Creating RingBuffer with invalid capacity throws
      """)
  void noNegativeCapacity(int capacity, boolean blocking) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new RingBuffer<String>(capacity, blocking)
    );
  }

  @ParameterizedTest
  @CsvSource(
      delimiterString = "|",
      useHeadersInDisplayName = true,
      textBlock = """
          capacity    | blocking
          1           | true
          2           | true
          1           | false
          2           | false
          """
  )
  @DisplayName("Builds instance with valid values")
  void validConstruct(int capacity, boolean blocking) {
    assertDoesNotThrow(
        () -> new RingBuffer<String>(capacity, blocking)
    );
  }
}
