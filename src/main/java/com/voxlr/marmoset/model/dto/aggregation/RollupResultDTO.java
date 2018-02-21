package com.voxlr.marmoset.model.dto.aggregation;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RollupResultDTO {
    private DateTime timestamp;
    private Object result;
    
    public RollupResultDTO(Object result) {
	this.result = result;
    }
}
