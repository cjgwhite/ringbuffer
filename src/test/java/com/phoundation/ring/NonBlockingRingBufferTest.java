package com.phoundation.ring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NonBlockingRingBufferTest {

  private RingBuffer<String> ringBuffer;

  @BeforeEach
  void setUp() {
    ringBuffer = new RingBuffer<>(3, false);
  }

  @Test
  @DisplayName("""
      Given an empty RingBuffer
      When I PUT an element
      Then READ returns same element
      """)
  void getsThePutElement() {
    String valueToPut = "VALUE";
    assertEquals(0, ringBuffer.size());
    ringBuffer.put(valueToPut);
    var valueRead = ringBuffer.get().get();

    assertEquals(valueToPut, valueRead);
  }

  @Test
  @DisplayName("""
      Given an empty RingBuffer
      When I PUT multiple elements
      Then READ returns elements in PUT order
      """)
  void getsTheFirstPutElement()  {
    String valueToPut = "VALUE";
    assertEquals(0, ringBuffer.size());
    ringBuffer.put(valueToPut + "First");
    ringBuffer.put(valueToPut + "Second");

    assertEquals(valueToPut+"First", ringBuffer.get().get());
    assertEquals(valueToPut+"Second", ringBuffer.get().get());
  }

  @Test
  @DisplayName("""
      Given an Full RingBuffer
      When I PUT an elements
      Then it returns true
      """)
  void trueIfPutWhenFull()  {
    while(!ringBuffer.isFull()) {
      ringBuffer.put("VALUE ");
    }
    assertTrue(ringBuffer.put("NEXT VALUE"));
  }

  @Test
  @DisplayName("""
      Given an Full RingBuffer
      When I PUT an elements
      Overwrites oldest Element
      """)
  void overwritesIfPutWHenFull()  {
    ringBuffer.put("VALUE 1"); // r=0 ; w=1
    ringBuffer.put("VALUE 2"); // r=1 ; w=0
    while(!ringBuffer.isFull()) { // isFull == TRUE
      ringBuffer.put("VALUE ");
    }
    ringBuffer.put("WILL OVERWRITE VALUE 1"); //

    System.out.println(ringBuffer);
    assertTrue(ringBuffer.isFull());
    assertEquals("VALUE 2", ringBuffer.get().get());
  }

  @Test
  @DisplayName("""
      Given an empty RingBuffer
      When READ an elements
      Then get empty optional
      """)
  void emptyOptionalIfEmpty()  {
    assertTrue(ringBuffer.isEmpty());

    assertTrue(ringBuffer.get().isEmpty());

    assertTrue(ringBuffer.isEmpty());
  }


  @Test
  @DisplayName("""
      A Drained buffer reads as empty
      """)
  void drainedToEmpty() {
    assertTrue(ringBuffer.isEmpty());

    ringBuffer.put("VALUE 1");
    ringBuffer.put("VALUE 2");
    ringBuffer.put("VALUE 3");


    ringBuffer.get();
    ringBuffer.get();
    ringBuffer.get();

    assertEquals(0, ringBuffer.size());
    assertTrue(ringBuffer.isEmpty());

  }

  @ParameterizedTest(name = "{index} When {0} added and {1} retrieved then size=={2}")
  @CsvSource(
      delimiterString = "|",
      textBlock =
//        add | get | expect
"""
          0   | 0   | 0
          0   | 1   | 0
          1   | 0   | 1
          2   | 0   | 2
          3   | 0   | 3
          4   | 0   | 3
          1   | 1   | 0
          2   | 1   | 1
          3   | 1   | 2
          4   | 1   | 2
          2   | 2   | 0
          3   | 3   | 0
          4   | 3   | 0
          4   | 4   | 0
          """
  )
  @DisplayName("Given RingBuffer of capacity 2")
  void testingSize(int add, int get, int expect) {
    for (int count=0; count < add; count++) {
      ringBuffer.put("VALUE "+count);
    }

    for (int count = 0; count < get; count++) {
      ringBuffer.get();
    }

    assertEquals(expect, ringBuffer.size());
  }
}
