package com.voxlr.marmoset.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;
import com.voxlr.marmoset.model.persistence.Entity;
import com.voxlr.marmoset.model.persistence.factory.EntityUpdate;

public class AtomicUpdateImpl<T extends Entity> implements AtomicUpdate<T> {
    
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public T update(EntityUpdate<T> update) {
	if (!update.isUpdateRequired()) {
	    return update.getEntity();
	}
	
	return update(update.getEntity(), update.getUpdate());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T update(T entity, Update update) {
	Query query = new Query(Criteria.where("id").is(entity.getId()));
	UpdateResult result = mongoOperations.updateFirst(
		query,
		update,
		entity.getClass());
	
	if (result.getModifiedCount() != 1) {
	    return entity;
	}
	
	return (T) mongoOperations.findOne(query, entity.getClass());
    }

}
