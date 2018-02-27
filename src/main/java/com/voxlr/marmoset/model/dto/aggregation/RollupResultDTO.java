package com.voxlr.marmoset.model.dto.aggregation;

import java.util.Map;

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
    private Map<String, Object> result;
    
    public RollupResultDTO(Map<String, Object>  result) {
	this.result = result;
    }
}
