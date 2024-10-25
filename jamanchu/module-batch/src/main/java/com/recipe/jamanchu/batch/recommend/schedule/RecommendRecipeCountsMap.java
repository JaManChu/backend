package com.recipe.jamanchu.batch.recommend.schedule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RecommendRecipeCountsMap implements Map<Long, Map<Long, Integer>> {

  private final Map<Long, Map<Long, Integer>> recipeCounts = new ConcurrentHashMap<>();

  @Override
  public int size() {
    return this.recipeCounts.size();
  }

  @Override
  public boolean isEmpty() {
    return this.recipeCounts.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.recipeCounts.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.recipeCounts.containsValue(value);
  }

  @Override
  public Map<Long, Integer> get(Object key) {
    return this.recipeCounts.get(key);
  }

  @Override
  public Map<Long, Integer> put(Long key, Map<Long, Integer> value) {
    return this.recipeCounts.put(key, value);
  }

  @Override
  public Map<Long, Integer> remove(Object key) {
    return this.recipeCounts.remove(key);
  }

  @Override
  public void putAll(Map<? extends Long, ? extends Map<Long, Integer>> m) {
    this.recipeCounts.putAll(m);
  }

  @Override
  public void clear() {
    this.recipeCounts.clear();
  }

  @Override
  public Set<Long> keySet() {
    return this.recipeCounts.keySet();
  }

  @Override
  public Collection<Map<Long, Integer>> values() {
    return this.recipeCounts.values();
  }

  @Override
  public Set<Entry<Long, Map<Long, Integer>>> entrySet() {
    return this.recipeCounts.entrySet();
  }
}
