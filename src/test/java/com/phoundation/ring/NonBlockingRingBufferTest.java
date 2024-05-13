package com.phoundation.ring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NonBlockingRingBufferTest {

  private RingBuffer<String> ringBuffer;

  @BeforeEach
  void setUp() {
    ringBuffer = new RingBuffer<>(2, false);
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
      Then it returns true
      """)
  void trueIfPutWHenFull()  {
    while(!ringBuffer.isFull()) {
      ringBuffer.put("VALUE ");
    }
    var size = ringBuffer.size();

    assertTrue(ringBuffer.put("NEXT VALUE"));

    assertEquals(size, ringBuffer.size());
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

    assertEquals(0, ringBuffer.size());
    assertTrue(ringBuffer.isEmpty());

  }

}
