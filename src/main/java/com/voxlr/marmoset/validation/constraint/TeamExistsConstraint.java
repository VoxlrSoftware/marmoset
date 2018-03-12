package com.voxlr.marmoset.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TeamExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamExistsConstraint {
  String message() default "Team must be valid.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
