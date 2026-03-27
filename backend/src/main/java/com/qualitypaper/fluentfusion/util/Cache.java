package com.qualitypaper.fluentfusion.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Builder
@AllArgsConstructor
public class Cache<K, V> {

  private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
  private final Map<K, Scheduled<V>> map;
  private final LinkedHashMap<K, Scheduled<V>> lruMap;
  private final ReentrantLock lock = new ReentrantLock();
  private final ScheduledExecutorService cleaner;
  private int capacity;
  private long timeout;

  public Cache(int capacity, long timeout) {
    this.capacity = capacity;
    this.timeout = timeout;
    this.map = new ConcurrentHashMap<>();
    this.lruMap = new LinkedHashMap<>(capacity, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, Scheduled<V>> eldest) {
        return this.size() > Cache.this.capacity;
      }
    };

    this.cleaner = Executors.newScheduledThreadPool(1);
    this.cleaner.schedule(() -> {
      int size = map.size();
      for (Map.Entry<K, Scheduled<V>> entry : map.entrySet()) {
        if (System.currentTimeMillis() - entry.getValue().millis() > timeout) {
          map.remove(entry.getKey());
          lruMap.remove(entry.getKey());
        }
      }
      log.info("Cleared cache, removed {}", size - map.size());
    }, timeout, TimeUnit.MILLISECONDS);
  }

  public Optional<V> get(K key) {
    lock.lock();
    try {
      if (!map.containsKey(key)) {
        return Optional.empty();
      }
      Scheduled<V> value = map.get(key);
      lruMap.get(key);
      return Optional.of(value.value());
    } finally {
      lock.unlock();
    }
  }

  public void put(K key, V value) {
    lock.lock();
    try {
      if (map.size() >= capacity) {
        K eldestKey = lruMap.keySet().iterator().next();
        map.remove(eldestKey);
        lruMap.remove(eldestKey);
      }
      Scheduled<V> scheduled = new Scheduled<>(value, System.currentTimeMillis());
      map.put(key, scheduled);
      lruMap.put(key, scheduled);
    } finally {
      lock.unlock();
    }
  }

  public void remove(K key) {
    lock.lock();
    try {
      if (map.containsKey(key)) {
        map.remove(key);
        lruMap.remove(key);
      }
    } finally {
      lock.unlock();
    }
  }

  public int size() {
    lock.lock();
    try {
      return map.size();
    } finally {
      lock.unlock();
    }
  }

  record Scheduled<V>(V value, long millis) {
  }
}
