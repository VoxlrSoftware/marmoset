package com.voxlr.marmoset.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleResponseDTO<T> {
  private T data;

  public SimpleResponseDTO(T data) {
    this.data = data;
  }
}
