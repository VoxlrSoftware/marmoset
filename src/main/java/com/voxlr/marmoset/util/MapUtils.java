package com.voxlr.marmoset.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.util.ListUtils.listOf;

public class MapUtils {

  @Getter
  @AllArgsConstructor
  public static class KVPair<K, V> {
    private K key;
    private V value;

    public static <K, V> KVPair<K, V> entry(K key, V value) {
      return new KVPair<>(key, value);
    }
  }

  public static <T, V> V getSafe(Map<T, V> map, T key, V defaultValue) {
    if (map.containsKey(key)) {
      return map.get(key);
    }

    return defaultValue;
  }

  @SafeVarargs
  public static <K, V> HashMap<K, V> mapOf(KVPair<K, V>... entries) {
    return (HashMap<K, V>)
        listOf(entries).stream().collect(Collectors.toMap(KVPair::getKey, KVPair::getValue));
  }
}
