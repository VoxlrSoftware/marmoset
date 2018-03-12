package com.voxlr.marmoset.convert;

import lombok.Getter;

@Getter
public abstract class ConvertResult<T> {
  private T result;

  public ConvertResult(T result) {
    this.result = result;
  }
}
