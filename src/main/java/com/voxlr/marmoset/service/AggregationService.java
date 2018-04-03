package com.voxlr.marmoset.service;

import com.voxlr.marmoset.aggregation.AbstractAggregation.RollupCadence;
import com.voxlr.marmoset.aggregation.field.AggFieldName;
import com.voxlr.marmoset.convert.TypeConverter;
import com.voxlr.marmoset.exception.ConvertException;
import com.voxlr.marmoset.exception.InvalidArgumentsException;
import com.voxlr.marmoset.util.EnumUtils;
import java.util.List;
import java.util.function.Supplier;

public abstract class AggregationService<T extends AggFieldName> extends ValidateableService {

  protected List<T> getFieldNames(List<String> fields, Class<T> fieldClass, Supplier<List<T>> fullList) throws Exception {
    if (fields.size() == 1 && fields.get(0).equalsIgnoreCase("true")) {
      return fullList.get();
    }

    try {
      return TypeConverter.convertList(fields, fieldClass, EnumUtils::convert);
    } catch (ConvertException e) {
      throw new InvalidArgumentsException(
          "Invalid arguments for param fields: ["
              + String.join(",", e.getNonConvertableStrings())
              + "]");
    }
  }

  protected RollupCadence getRollupCadence(String cadence) throws Exception {
    try {
      return TypeConverter.convert(cadence, RollupCadence.class, EnumUtils::convert);
    } catch (ConvertException e) {
      throw new InvalidArgumentsException(
          "Invalid arguments for param fields: ["
              + String.join(",", e.getNonConvertableStrings())
              + "]");
    }
  }

  protected abstract List<T> getFieldNames(List<String> fields) throws Exception;
}
