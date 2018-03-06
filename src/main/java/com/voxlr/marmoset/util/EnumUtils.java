package com.voxlr.marmoset.util;

import java.lang.reflect.Method;

import com.voxlr.marmoset.model.ConvertibleEnum;

public class EnumUtils {
    
    @SuppressWarnings("unchecked")
    public static <T extends ConvertibleEnum> T convert(String source, Class<T> enumClass) {
	T result = null;
	try {
	    Method method = enumClass.getDeclaredMethod("fromString", String.class);
	    result = (T) method.invoke(null, (String)source);
	} catch (Exception e) {}
	
	return result;
    }
}
