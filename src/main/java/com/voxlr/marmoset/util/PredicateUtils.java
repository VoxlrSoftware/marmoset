package com.voxlr.marmoset.util;

import java.util.function.Predicate;

public class PredicateUtils {

  public static <T> Predicate<T> instanceOfFilter(Class<?> clazz) {
    return new Predicate<T>() {

      @Override
      public boolean test(T t) {
        return clazz.isAssignableFrom(t.getClass());
      }
    };
  }
}
