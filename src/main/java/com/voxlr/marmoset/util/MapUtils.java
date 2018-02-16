package com.voxlr.marmoset.util;

import java.util.Map;

public class MapUtils {
    public static <T,V> V getSafe(Map<T, V> map, T key, V defaultValue) {
	if (map.containsKey(key)) {
	    return map.get(key);
	}
	
	return defaultValue;
    }
}
