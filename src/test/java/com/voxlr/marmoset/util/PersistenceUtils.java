package com.voxlr.marmoset.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.voxlr.marmoset.model.persistence.Entity;

public class PersistenceUtils {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void save(Entity... entities) {
	for (int i = 0; i < entities.length; i++) {
	    Entity entity = entities[i];
	    mongoTemplate.save(entity);
	    updateEntity(entity);
	}
    }
    
    public void removeAll(String collectionName) {
	mongoTemplate.findAllAndRemove(new Query(), collectionName);
    }
    
    @SuppressWarnings("unchecked")
    public void removeAll(Class className) {
	mongoTemplate.findAllAndRemove(new Query(), className);
    }
    
    public <T extends Entity> void removeAllAndSave(Class<T> className, T... entities) {
	removeAll(className);
	save(entities);
    }
    
    public void updateEntity(Entity entity) {
	entity = mongoTemplate.findById(entity.getId(), entity.getClass());
    }
}
