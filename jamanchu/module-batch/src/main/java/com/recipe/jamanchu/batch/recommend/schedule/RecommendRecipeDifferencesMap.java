package com.recipe.jamanchu.batch.recommend.schedule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendRecipeDifferencesMap implements Map<Long, Map<Long,Double>> {

  private final Map<Long, Map<Long,Double>> recipeDifferences = new ConcurrentHashMap<>();

  @Override
  public int size() {
    return this.recipeDifferences.size();
  }

  @Override
  public boolean isEmpty() {
    return this.recipeDifferences.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.recipeDifferences.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.recipeDifferences.containsValue(value);
  }

  @Override
  public Map<Long, Double> get(Object key) {
    return this.recipeDifferences.get(key);
  }

  @Override
  public Map<Long, Double> put(Long key, Map<Long, Double> value) {
    return this.recipeDifferences.put(key, value);
  }

  @Override
  public Map<Long, Double> remove(Object key) {
    return this.recipeDifferences.remove(key);
  }

  @Override
  public void putAll(Map<? extends Long, ? extends Map<Long, Double>> m) {
    this.recipeDifferences.putAll(m);
  }

  @Override
  public void clear() {
    this.recipeDifferences.clear();
  }

  @Override
  public Set<Long> keySet() {
    return this.recipeDifferences.keySet();
  }

  @Override
  public Collection<Map<Long, Double>> values() {
    return this.recipeDifferences.values();
  }

  @Override
  public Set<Entry<Long, Map<Long, Double>>> entrySet() {
    return this.recipeDifferences.entrySet();
  }

  @Override
  public Map<Long, Double> getOrDefault(Object key, Map<Long, Double> defaultValue) {
    return Map.super.getOrDefault(key, defaultValue);
  }
}
