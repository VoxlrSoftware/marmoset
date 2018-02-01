package com.voxlr.marmoset.model.persistence.factory;

import org.springframework.data.mongodb.core.query.Update;

import com.voxlr.marmoset.model.persistence.Entity;

import lombok.Getter;

public abstract class EntityUpdate<T extends Entity> {
    @Getter
    private T entity;
    private Update update;
    
    public EntityUpdate(T entity) {
	this.entity = entity;
    }
    
    public Update getUpdate() {
	if (update != null) {
	    update = new Update();
	}
	
	return update;
    }
    
    public boolean isUpdateRequired() {
	return update != null;
    }
}
