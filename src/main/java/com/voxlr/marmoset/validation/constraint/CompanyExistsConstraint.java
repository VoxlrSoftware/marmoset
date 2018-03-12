package com.voxlr.marmoset.validation.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CompanyExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompanyExistsConstraint {
  String message() default "Company must be valid.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
