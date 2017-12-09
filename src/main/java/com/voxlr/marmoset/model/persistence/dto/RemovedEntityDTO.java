package com.voxlr.marmoset.model.persistence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemovedEntityDTO {
    private String id;
    private boolean deleted = true;
    
    public RemovedEntityDTO(String id) {
	this.id = id;
    }
}
