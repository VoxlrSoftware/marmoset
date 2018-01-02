package com.voxlr.marmoset.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

public class AnnotationUtils {
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Set<Class<?>> getAnnotatedClasses(
	    String packageName,
	    Class annotationClass) {
	return new Reflections(packageName).getTypesAnnotatedWith(annotationClass);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> HashMap<String, Object> getAnnotationMembers(
	    Class annotatedClass, 
	    Class annotationClass) {
	HashMap<String, Object> methodMap = new HashMap<>();
	Annotation annotation = annotatedClass.getAnnotation(annotationClass);
	Arrays.stream(annotation.annotationType().getDeclaredMethods()).forEach(method -> {
	    try {		
		Object value = method.invoke(annotation);
		methodMap.put(method.getName(), value);
	    } catch (Exception e) {
		throw new RuntimeException("Invalid method name for annotatedClass [" +
			annotatedClass.getName() + "].");
	    }
	});
	
	return methodMap;
    }
}
