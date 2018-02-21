package com.voxlr.marmoset.model.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RollupResultDTO {
    private String name;
    private Object result;
    
    public RollupResultDTO(Object result) {
	this.result = result;
    }
}
