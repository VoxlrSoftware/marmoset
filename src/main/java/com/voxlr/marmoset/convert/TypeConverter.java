package com.voxlr.marmoset.convert;

import com.voxlr.marmoset.exception.ConvertException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.util.PredicateUtils.instanceOfFilter;

public class TypeConverter {

  public static <T, U> Function<U, ConvertResult<T>> getConvertFunction(
      Class<T> clazz, BiFunction<U, Class<T>, T> converter) {
    return (source) -> {
      try {
        T value = converter.apply(source, clazz);

        if (value == null) {
          throw new Exception();
        }

        return new ConvertSuccessResult<>(converter.apply(source, clazz));
      } catch (Exception e) {
        return new ConvertFailedResult<>(source);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <T, U> T convert(U input, Class<T> clazz, BiFunction<U, Class<T>, T> converter)
      throws ConvertException {
    ConvertResult<T> result = getConvertFunction(clazz, converter).apply(input);

    if (result instanceof ConvertFailedResult) {
      throw new ConvertException(((ConvertFailedResult<T, U>) result).getSource());
    }

    return result.getResult();
  }

  @SuppressWarnings("unchecked")
  public static <T, U> List<T> convertList(
      List<U> input, Class<T> clazz, BiFunction<U, Class<T>, T> converter) throws ConvertException {
    List<ConvertResult<T>> convertResults =
        input.stream().map(getConvertFunction(clazz, converter)).collect(Collectors.toList());

    List<U> failedResults =
        convertResults
            .stream()
            .filter(instanceOfFilter(ConvertFailedResult.class))
            .map(result -> ((ConvertFailedResult<T, U>) result).getSource())
            .collect(Collectors.toList());

    if (failedResults.size() > 0) {
      throw new ConvertException(failedResults);
    }

    return convertResults.stream().map(ConvertResult::getResult).collect(Collectors.toList());
  }
}
