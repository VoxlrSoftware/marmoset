package com.voxlr.marmoset.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtils {

    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
	return Arrays.stream(items).collect(Collectors.toList());
    }
    
    public static <T1, T2> List<T2> mapList(List<T1> initial, Function<T1, T2> mapper) {
	return initial.stream().map(mapper).collect(Collectors.toList());
    }
}
