package com.voxlr.marmoset.aggregation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RollupResultDTO {
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private DateTime timestamp;

  private AggregateResultDTO result;

  public RollupResultDTO(AggregateResultDTO result) {
    this.result = result;
  }
}
