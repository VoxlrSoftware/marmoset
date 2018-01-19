package com.voxlr.marmoset.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.voxlr.marmoset.model.persistence.Entity;

@Service
public class MongoService {
    
    @Autowired
    private MongoOperations mongoOperations;

    @SuppressWarnings("unchecked")
    public <T extends Entity> T update(T entity, Update update) {
	Query query = new Query(Criteria.where("id").is(entity.getId()));
	WriteResult result = mongoOperations.updateFirst(
		query,
		update,
		entity.getClass());
	
	if (result.getN() != 1) {
	    return entity;
	}
	
	return (T) mongoOperations.findOne(query, entity.getClass());
    }
}
