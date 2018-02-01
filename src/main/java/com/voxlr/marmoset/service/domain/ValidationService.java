package com.voxlr.marmoset.service.domain;

import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotatedClasses;
import static com.voxlr.marmoset.util.AnnotationUtils.getAnnotationMembers;

import java.util.HashMap;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.voxlr.marmoset.model.AuthUser;
import com.voxlr.marmoset.validation.validator.TypeValidator;
import com.voxlr.marmoset.validation.validator.Validator;

@Service
public class ValidationService {
    
    @SuppressWarnings("rawtypes")
    private final HashMap<Class<?>, Validator> validators = 
	    new HashMap<>();
    
    public ValidationService() {
	Set<Class<?>> validators = 
		getAnnotatedClasses("com.voxlr.marmoset.validation.validator", TypeValidator.class);
	
	validators.stream().forEach(validationClass -> {
	    try {
		if (Validator.class.isAssignableFrom(validationClass)) {
			Validator<?> validator = (Validator<?>) validationClass.newInstance();
			HashMap<String, Object> methodMap = getAnnotationMembers(
				validationClass,
				TypeValidator.class);
			Class<?> repClass = (Class<?>) methodMap.get("forClass");
			if (repClass != null) {
			    this.validators.put(repClass, validator);
			}
		    }
	    } catch (Exception e) {
		throw new RuntimeException("Illegal use of @TestAnnotation", e);
	    }
	});
    }
    
    @SuppressWarnings("unchecked")
    public  void validate(AuthUser authUser, Object entity) {
	Class<?> clazz = entity.getClass();
	if (validators.containsKey(clazz)) {
	    validators.get(clazz).validate(authUser, entity);
	}
    }
}
