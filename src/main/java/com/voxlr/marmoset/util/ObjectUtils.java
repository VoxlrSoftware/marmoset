package com.voxlr.marmoset.util;

import java.util.function.Function;
import java.util.function.Predicate;

public class ObjectUtils {
  public static <T, R> Predicate<T> isEqual(Function<? super T, ? extends R> f, R value) {
    return value == null ? t -> f.apply(t) == null : t -> value.equals(f.apply(t));
  }
}
