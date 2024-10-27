package com.recipe.jamanchu.domain.component.bean;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsSet implements Map<String, LocalDateTime> {

  private final Set<String> dailyVisitors = ConcurrentHashMap.newKeySet();

  @Override
  public int size() {
    return this.dailyVisitors.size();
  }

  @Override
  public boolean isEmpty() {
    return this.dailyVisitors.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.dailyVisitors.contains(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.dailyVisitors.contains(value);
  }

  @Override
  public LocalDateTime get(Object key) {
    return this.dailyVisitors.contains(key) ? LocalDateTime.now() : null;
  }

  @Override
  public LocalDateTime put(String key, LocalDateTime value) {
    return this.dailyVisitors.add(key) ? LocalDateTime.now() : null;
  }

  @Override
  public LocalDateTime remove(Object key) {
    return this.dailyVisitors.remove(key) ? LocalDateTime.now() : null;
  }

  @Override
  public void putAll(Map<? extends String, ? extends LocalDateTime> m) {
    this.dailyVisitors.addAll(m.keySet());
  }

  @Override
  public void clear() {
    this.dailyVisitors.clear();
  }

  @Override
  public Set<String> keySet() {
    return this.dailyVisitors.isEmpty() ? Set.of() : this.dailyVisitors;
  }

  @Override
  public Collection<LocalDateTime> values() {
    return this.dailyVisitors.isEmpty() ? List.of() : List.of(LocalDateTime.now());
  }

  @Override
  public Set<Entry<String, LocalDateTime>> entrySet() {
    return this.dailyVisitors.isEmpty() ? Set.of() : Set.of(Map.entry(this.dailyVisitors.iterator().next(), LocalDateTime.now()));
  }
}
