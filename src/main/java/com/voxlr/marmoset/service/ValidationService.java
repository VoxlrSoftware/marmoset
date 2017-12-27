package com.voxlr.marmoset.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.validation.validator.TypeValidator;
import com.voxlr.marmoset.validation.validator.Validator;

@Service
public class ValidationService {
    
    private final HashMap<Class, Validator> validators = 
	    new HashMap<>();
    
    public ValidationService() {
	Set<Class<?>> validators = 
		new Reflections("com.voxlr.marmoset").getTypesAnnotatedWith(TypeValidator.class);
	
	validators.stream().forEach(validationClass -> {
	    try {
		if (Validator.class.isAssignableFrom(validationClass)) {
			Validator validator = (Validator) validationClass.newInstance();
			
			Annotation testAnnotation = validationClass.getAnnotation(TypeValidator.class);
			Method forClass = testAnnotation.annotationType().getMethod("forClass");
			Class repClass = (Class) forClass.invoke(testAnnotation);
			if (repClass != null) {
			    this.validators.put(repClass, validator);
			}
		    }
	    } catch (Exception e) {
		throw new RuntimeException("Illegal use of @TestAnnotation", e);
	    }
	    
	});
    }
    
    public void validate(AuthUser authUser, Object entity) {
	Class clazz = entity.getClass();
	if (validators.containsKey(clazz)) {
	    validators.get(clazz).validate(authUser, entity);
	}
    }
}
