package com.phoundation.ring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BlockingRingBufferTest {

  private RingBuffer<String> ringBuffer;

  @BeforeEach
  void setUp() {
    ringBuffer = new RingBuffer<>(2, true);
  }

  @Test
  @DisplayName("Add element increases size")
  void putElementIncreasesSize()  {
    var startSize = ringBuffer.size();
    ringBuffer.put("VALUE");

    assertEquals(startSize+1, ringBuffer.size());
  }

  @Test
  @DisplayName("Read element decreases size")
  void readElementDecreasesSize() {

    ringBuffer.put("VALUE");
    var startSize = ringBuffer.size();
    ringBuffer.get();

    assertEquals(startSize-1, ringBuffer.size());
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

}
