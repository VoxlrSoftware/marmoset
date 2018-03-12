package com.voxlr.marmoset.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.util.ListUtils.listOf;

@Getter
public class ConvertException extends Exception {
  private static final long serialVersionUID = -4389959557275112278L;

  private List<? extends Object> nonConvertableObjects;

  public ConvertException(List<? extends Object> nonConvertableObjects) {
    this.nonConvertableObjects = nonConvertableObjects;
  }

  public ConvertException(Object nonConvertableObject) {
    this.nonConvertableObjects = listOf(nonConvertableObject);
  }

  public List<String> getNonConvertableStrings() {
    return nonConvertableObjects.stream().map(Object::toString).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Unable to convert one or more source objects [");
    builder.append(String.join(", ", getNonConvertableStrings()));
    builder.append("]");
    return builder.toString();
  }
}
