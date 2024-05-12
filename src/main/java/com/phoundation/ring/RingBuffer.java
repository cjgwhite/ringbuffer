package com.phoundation.ring;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RingBuffer<T> {

  private static final int DEFAULT_CAPACITY = 1000;
  private final AtomicInteger writeIndex = new AtomicInteger(0);
  private final AtomicInteger readIndex = new AtomicInteger(0);
  private final int capacity;

  private final T[] buffer;

  public RingBuffer(int capacity) {
    this.capacity = capacity < 1 ? DEFAULT_CAPACITY : capacity;
    this.buffer = (T[]) new Object[capacity];
  }

  public RingBuffer() {
    this.capacity = DEFAULT_CAPACITY;
    this.buffer = (T[]) new Object[this.capacity];
  }

  public int size() {
    var write = writeIndex.get();
    var read = readIndex.get();

    var diff = write - read;

    return diff < 0 ?
      capacity - read + write :
      diff;

  }

  public boolean isFull() {

    return size() == capacity-1;
  }

  public boolean isEmpty() {
    return writeIndex.get() == readIndex.get();
  }

  private int nextVal(int val) {
    return (val+1) % capacity;
  }

  public synchronized boolean put(T value) {
    if (isFull()) {
      return false;
    }

    buffer[writeIndex.getAndUpdate(this::nextVal)] = value;
    return true;
  }

  public synchronized Optional<T> read() {
    return isEmpty() ?
               Optional.empty()
               : Optional.of(buffer[readIndex.getAndUpdate(this::nextVal)]);
  }
}
