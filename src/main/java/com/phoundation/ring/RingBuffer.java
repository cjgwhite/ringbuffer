package com.phoundation.ring;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RingBuffer<T>  {

  private final AtomicBoolean putLast = new AtomicBoolean(false);
  private final AtomicInteger writeIndex = new AtomicInteger(0);
  private final AtomicInteger readIndex = new AtomicInteger(0);
  private final int capacity;
  private final boolean blocking;
  private final T[] buffer;

  RingBuffer(int capacity, boolean blocking) {
    if (capacity < 1) {
      throw new IllegalArgumentException("Capacity <1");
    }

    this.blocking = blocking;
    this.capacity = capacity;
    this.buffer = (T[]) new Object[capacity];
  }

  public final int size() {

    if (isFull()) {
      return capacity;
    }

    if (isEmpty()) {
      return 0;
    }

    return Math.floorMod(currentWritePointer() - currentReadPointer(), capacity);
  }

  public final boolean isFull() {
    return putLast.get() && currentWritePointer() == currentReadPointer();
  }

  public final boolean isEmpty() {
    return !putLast.get() && currentReadPointer() == currentWritePointer();
  }

  public final synchronized Optional<T> get() {

    if (isEmpty()) {
      return Optional.empty();
    }
    putLast.set(false);
    return Optional.of(buffer[advanceReadPointer()]);

  }

  public synchronized boolean put(T value) {
    if (isFull()) {
      if (blocking) {
        return false;
      }

      advanceReadPointer();
    }
    add(value);

    return true;
  }

  private int nextVal(int val) {
    return (val+1) % capacity;
  }

  private void add(T value) {
    putLast.set(true);
    buffer[advanceWritePointer()] = value;
  }

  private int advanceWritePointer() {
    return writeIndex.getAndUpdate(this::nextVal);
  }

  private int advanceReadPointer() {
    return readIndex.getAndUpdate(this::nextVal);
  }

  private int currentReadPointer() {
    return readIndex.get();
  }

  private int currentWritePointer() {
    return writeIndex.get();
  }
}
