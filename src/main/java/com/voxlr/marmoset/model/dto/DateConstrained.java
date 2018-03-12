package com.voxlr.marmoset.model.dto;

import lombok.*;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateConstrained {
  @NotNull(groups = {StartDateConstrained.class, DateConstrained.class})
  private DateTime startDate;

  @NotNull(groups = {EndDateConstrained.class, DateConstrained.class})
  private DateTime endDate;

  public interface StartDateConstrained {}

  public interface EndDateConstrained {}
}
