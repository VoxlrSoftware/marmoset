package com.voxlr.marmoset.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T> List<T> listOf(T... items) {
	return Arrays.stream(items).collect(Collectors.toList());
    }
}
