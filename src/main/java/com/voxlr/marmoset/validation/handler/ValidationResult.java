package com.voxlr.marmoset.validation.handler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResult<T> {
  T result;

  @Setter(AccessLevel.NONE)
  boolean resultSet = false;

  public void setResult(T result) {
    this.result = result;
    this.resultSet = true;
  }
}
