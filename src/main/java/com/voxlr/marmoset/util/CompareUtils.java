package com.voxlr.marmoset.util;

public class CompareUtils {

  public static <T> boolean safeEquals(T valA, T valB) {
    return valA == null ? valB == null : valA.equals(valB);
  }
}
