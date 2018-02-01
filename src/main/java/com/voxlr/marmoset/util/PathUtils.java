package com.voxlr.marmoset.util;

import static com.google.common.base.Strings.isNullOrEmpty;

public class PathUtils {
    public static String combinePaths(String... paths) {
	String initial = "";
	for (String path : paths) {
	    initial = reducePaths(initial, path);
	}
	
	return initial;
    }
    
    public static String reducePaths(String combined, String path) {
	if (isNullOrEmpty(path)) {
	    return combined;
	}
	
	String returnVal = path;
	
	if (combined.length() > 0 && returnVal.charAt(0) != '/') {
	    returnVal = "/" + returnVal;
	}
	
	if (returnVal.charAt(returnVal.length() - 1) == '/') {
	    returnVal = returnVal.substring(0, returnVal.length() - 1);
	}
	
	return combined + returnVal;
    }
}
