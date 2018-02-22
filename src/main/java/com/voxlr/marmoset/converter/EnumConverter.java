package com.voxlr.marmoset.converter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import com.voxlr.marmoset.model.ConvertibleEnum;

public class EnumConverter implements ConditionalGenericConverter{

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
	return Collections.singleton(new ConvertiblePair(String.class, ConvertibleEnum.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
	try {
	    Method method = targetType.getType().getDeclaredMethod("fromString", String.class);
	    return method.invoke(null, (String)source);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	return null;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
	return ConvertibleEnum.class.isAssignableFrom(targetType.getType());
    }

}
