package com.recipe.jamanchu.api.notify;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseEmitterMap implements Map<Long, SseEmitter> {

  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

  @Override
  public int size() {
    return this.emitters.size();
  }

  @Override
  public boolean isEmpty() {
    return this.emitters.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.emitters.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.emitters.containsValue(value);
  }

  @Override
  public SseEmitter get(Object key) {
    return this.emitters.get(key);
  }

  @Override
  public SseEmitter put(Long key, SseEmitter value) {
    return this.emitters.put(key, value);
  }

  @Override
  public SseEmitter remove(Object key) {
    return this.emitters.remove(key);
  }

  @Override
  public void putAll(Map<? extends Long, ? extends SseEmitter> m) {
    this.emitters.putAll(m);
  }

  @Override
  public void clear() {
    this.emitters.clear();
  }

  @Override
  public Set<Long> keySet() {
    return this.emitters.keySet();
  }

  @Override
  public Collection<SseEmitter> values() {
    return this.emitters.values();
  }

  @Override
  public Set<Entry<Long, SseEmitter>> entrySet() {
    return this.emitters.entrySet();
  }
}
