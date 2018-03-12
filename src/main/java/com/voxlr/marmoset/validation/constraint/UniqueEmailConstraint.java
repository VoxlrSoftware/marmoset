package com.voxlr.marmoset.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmailConstraint {
  String message() default "Email must be unique.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
