package com.voxlr.marmoset.validation.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeValidator {
    @SuppressWarnings("rawtypes")
    Class forClass();
}
