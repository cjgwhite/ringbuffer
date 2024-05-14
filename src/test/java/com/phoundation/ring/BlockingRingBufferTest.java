package com.phoundation.ring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BlockingRingBufferTest {

  private RingBuffer<String> ringBuffer;

  @BeforeEach
  void setUp() {
    ringBuffer = new RingBuffer<>(4, true);
  }

  @Test
  @DisplayName("""
      Given an empty RingBuffer
      When I PUT an element
      Then READ returns same element
      And size is 0
      """)
  void getsThePutElement() {
    String valueToPut = "VALUE";
    assertTrue(ringBuffer.isEmpty());
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
      Then it returns false
      """)
  void falseIfPutWHenFull()  {
    while(!ringBuffer.isFull()) {
      ringBuffer.put("VALUE ");
    }
    var size = ringBuffer.size();

    assertFalse(ringBuffer.put("NEXT VALUE"));

    assertEquals(size, ringBuffer.size());
  }

  @Test
  @DisplayName("""
      Given an empty RingBuffer
      When READ an elements
      Then get empty optional
      """)
  void emptyOptionalIfEmpty()  {
    assertTrue(ringBuffer.isEmpty());
    assertEquals(0, ringBuffer.size());

    assertTrue(ringBuffer.get().isEmpty());

    assertTrue(ringBuffer.isEmpty());
    assertEquals(0, ringBuffer.size());
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

    assertTrue(ringBuffer.isEmpty());
    assertEquals(0, ringBuffer.size());
  }

  @Test
  @DisplayName("""
      Given Full buffer
      When read element
      Then can add another element""")
  void writeAfterRead() {
    while(!ringBuffer.isFull()) {
      ringBuffer.put("VALUE ");
    }

    ringBuffer.get();

    assertTrue(ringBuffer.put("ANOTHER VALUE"));
    assertFalse(ringBuffer.put("NO SPACE FOR THIS ONE"));
  }

  @ParameterizedTest(name = "{index} When {0} added and {1} retrieved then size=={2}")
  @CsvSource(
      delimiterString = "|",
      textBlock =
//        add | get | expect
          """
          0   | 0   | 0
          1   | 0   | 1
          2   | 0   | 2
          3   | 0   | 3
          4   | 0   | 4
          5   | 0   | 4
          0   | 1   | 0
          1   | 1   | 0
          2   | 1   | 1
          3   | 1   | 2
          4   | 1   | 3
          5   | 1   | 3
          2   | 2   | 0
          3   | 2   | 1
          4   | 2   | 2
          5   | 2   | 2
          3   | 3   | 0
          4   | 3   | 1
          5   | 3   | 1
          4   | 4   | 0
          5   | 4   | 0
                    """
  )
  @DisplayName("Given RingBuffer of capacity 4")
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
