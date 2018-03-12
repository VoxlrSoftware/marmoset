package com.voxlr.marmoset.validation.handler;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.model.AuthUser;

import java.util.function.Consumer;

public abstract class ValidationHandler<T> {

  public void validate(AuthUser authUser, Supplier<T> getter) throws Exception {
    ValidationResult<T> result = new ValidationResult<>();
    validate(authUser, getter.get(), result);
  }

  public void validate(AuthUser authUser, Supplier<T> getter, Consumer<T> setter) throws Exception {
    ValidationResult<T> result = new ValidationResult<>();
    validate(authUser, getter.get(), result);

    if (setter != null && result.resultSet) {
      setter.accept(result.getResult());
    }
  }

  abstract void validate(AuthUser authUser, T input, ValidationResult<T> result) throws Exception;
}
