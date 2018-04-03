package com.voxlr.marmoset.aggregation.field;

import com.fasterxml.jackson.annotation.JsonValue;
import com.voxlr.marmoset.model.ConvertibleEnum;

public interface AggFieldName extends ConvertibleEnum {

  @JsonValue
  String getName();
}
