package com.voxlr.marmoset.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberValidConstraint {
  String message() default "Phone number must be valid.";

  boolean required() default false;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
