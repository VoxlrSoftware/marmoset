package com.voxlr.marmoset.aggregation.dto;

import java.util.HashMap;
import java.util.Map;

public class AggregateResultDTO extends HashMap<String, Object>{
    private static final long serialVersionUID = 2332220866417701502L;
    
    public AggregateResultDTO() {
	super();
    }
    
    public AggregateResultDTO(Map<String, Object> result) {
	super(result);
    }
}
