package com.voxlr.marmoset.model.dto.aggregation;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RollupResultDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private DateTime timestamp;
    private Object result;
    
    public RollupResultDTO(Object result) {
	this.result = result;
    }
}
