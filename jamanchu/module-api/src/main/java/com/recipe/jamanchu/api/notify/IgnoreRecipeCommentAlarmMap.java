package com.recipe.jamanchu.api.notify;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IgnoreRecipeCommentAlarmMap implements Map<Long, Set<Long>> {

  private final Map<Long, Set<Long>> ignoreAlarmRecipeIds = new ConcurrentHashMap<>();

  @Override
  public int size() {
    return this.ignoreAlarmRecipeIds.size();
  }

  @Override
  public boolean isEmpty() {
    return this.ignoreAlarmRecipeIds.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.ignoreAlarmRecipeIds.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.ignoreAlarmRecipeIds.containsValue(value);
  }

  @Override
  public Set<Long> get(Object key) {
    return this.ignoreAlarmRecipeIds.get(key);
  }

  @Override
  public Set<Long> put(Long key, Set<Long> value) {
    return this.ignoreAlarmRecipeIds.put(key, value);
  }

  @Override
  public Set<Long> remove(Object key) {
    return this.ignoreAlarmRecipeIds.remove(key);
  }

  @Override
  public void putAll(Map<? extends Long, ? extends Set<Long>> m) {
    this.ignoreAlarmRecipeIds.putAll(m);
  }

  @Override
  public void clear() {
    this.ignoreAlarmRecipeIds.clear();
  }

  @Override
  public Set<Long> keySet() {
    return this.ignoreAlarmRecipeIds.keySet();
  }

  @Override
  public Collection<Set<Long>> values() {
    return this.ignoreAlarmRecipeIds.values();
  }

  @Override
  public Set<Entry<Long, Set<Long>>> entrySet() {
    return this.ignoreAlarmRecipeIds.entrySet();
  }
}
